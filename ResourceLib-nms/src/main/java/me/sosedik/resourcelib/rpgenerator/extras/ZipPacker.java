package me.sosedik.resourcelib.rpgenerator.extras;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.rpgenerator.ResourcePackGenerator;
import me.sosedik.utilizer.util.FileUtil;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ZipPacker {

	private static final String ZIP_NAME = "resource_pack.zip";

	private long centralOffset;
	private long entries;
	private long endStart;

	public @NotNull File packZip(@NotNull ResourcePackGenerator generator) {
		var outputFile = new File(generator.getDataDir(), ZIP_NAME);
		FileUtil.deleteFile(outputFile);
		try (var zip = new ZipFile(outputFile)) {
			var zipParameters = new ZipParameters();
			zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
			for (File file : Objects.requireNonNull(generator.getOutputDir().listFiles())) {
				if (file.isDirectory())
					zip.addFolder(file, zipParameters);
				else if (file.getName().equals("pack.mcmeta"))
					zip.addFile(file, zipParameters);
			}
			if (generator.getPackOptions().shouldCorrupt())
				corruptArchive(outputFile);
			clearMeta(outputFile);
		} catch (IOException e) {
			ResourceLib.logger().error("Couldn't create zip file", e);
			return outputFile;
		}
		ResourceLib.logger().info("Created zip file");
		return outputFile;
	}

	public static void clearMeta(@NotNull File file) {
		if (file.isDirectory()) {
			for (File f : Objects.requireNonNull(file.listFiles()))
				clearMeta(f);
		}
		try {
			var path = file.toPath();
			BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
			// lastModifiedTime, lastAccessTime, creationTime
			basicView.setTimes(FileTime.fromMillis(0), FileTime.fromMillis(0), FileTime.fromMillis(0));
		} catch (Exception e) {
			ResourceLib.logger().error("Couldn't clear file meta for {}", file.getAbsolutePath(), e);
		}
		if (!file.isDirectory() && !file.setReadOnly())
			ResourceLib.logger().warn("[ZipPacker] Could not set read-only attribute for {}!", file.getName());
	}

	private void corruptArchive(@NotNull File outputFile) throws IOException {
		// Bits: https://en.wikipedia.org/wiki/ZIP_(file_format)#Central_directory_file_header
		// https://users.cs.jmu.edu/buchhofp/forensics/formats/pkzip.html
		List<Byte> local = new ArrayList<>();
		List<Byte> central = new ArrayList<>();
		List<Byte> ending = new ArrayList<>();
		long locOff = 0;
		calcCentralHeader(outputFile);
		try (var raf = new RandomAccessFile(outputFile, "rw")) {
			if (centralOffset > raf.length())
				ResourceLib.logger().error("[Packer] Wrong central offset: {} | {}", raf.length(), centralOffset);
			long offset = centralOffset;
			var check = 0;
			var entry = 0;
			while (entry < entries && offset < raf.length()) {
				raf.seek(offset);
				for (var i = 0; i < 4; i++) {
					offset++;
					var b = raf.readByte();
					if (isCentral(check, b))
						check++;
					else
						break;
				}
				if (check == 4) {
					entry++;
					// Central header
					StringBuilder sb;
					long centralOffset = offset - 4;

					raf.seek(centralOffset + 4);
					raf.write(Byte.MAX_VALUE); // Made with version

					raf.seek(centralOffset + 6);
					raf.write(Byte.MAX_VALUE); // Version needed to extract

					raf.seek(centralOffset + 8);
					for (var i = 0; i < 2; i++) raf.write(0); // General purpose bit flag

					raf.seek(centralOffset + 12);
					for (var i = 0; i < 4; i++) raf.write(0); // Clear Modified dates

					raf.seek(centralOffset + 16);
					for (var i = 0; i < 4; i++) raf.write(0); // Corrupt CRC32 checksum

					raf.seek(centralOffset + 35);
					raf.write(1); // Corrupt disk number

					raf.seek(centralOffset + 38);
					raf.write(1); // External file attributes
					for (var i = 0; i < 3; i++) raf.write(0);

					// Get file name length
					raf.seek(centralOffset + 29);
					sb = new StringBuilder();
					sb.append(getB(raf.readByte()));
					raf.seek(centralOffset + 28);
					sb.append(getB(raf.readByte()));
					long cNameLength = Integer.parseInt(sb.toString(), 16);

					// Read file name
					sb = new StringBuilder();
					raf.seek(centralOffset + 46);
					for (var i = 0; i < cNameLength; i++)
						sb.append((char) raf.readByte());
					var fileName = sb.toString();
					// Sound files can't be played otherwise
					if (!fileName.endsWith(".ogg")) {
						raf.seek(centralOffset + 24);
						for (var i = 0; i < 4; i++) raf.write(0); // Uncompressed size
					}

					// Get extra field length
					raf.seek(centralOffset + 31);
					sb = new StringBuilder();
					sb.append(getB(raf.readByte()));
					raf.seek(centralOffset + 30);
					sb.append(getB(raf.readByte()));
					long cExtraLength = Integer.parseInt(sb.toString(), 16);

					for (var i = 0; i < 4; i++) {
						raf.seek(--centralOffset + 42 + 4);
						sb.append(getB(raf.readByte()));
					}
					centralOffset += 4;

					// Current local header offset
					var localOffset = Long.parseLong(sb.toString(), 16);

					// Update local header offset
					raf.seek(centralOffset + 42);
					byte[] ama = longToBytes(localOffset - locOff);
					ArrayUtils.reverse(ama);
					ama = Arrays.copyOf(ama, 4);
					for (byte b : ama) raf.write(b);

					raf.seek(centralOffset);
					for (var i = 0; i < 46 + cNameLength + cExtraLength; i++)
						central.add(raf.readByte());

					// Local header
					raf.seek(localOffset + 4L);
					raf.write(Byte.MAX_VALUE); // Version needed to extract

					raf.seek(localOffset + 6L);
					for (var i = 0; i < 2; i++) raf.write(0); // Clear General purpose bit flag

					raf.seek(localOffset + 10L);
					for (var i = 0; i < 4; i++) raf.write(0); // Clear Modification dates

					raf.seek(offset + 14L);
					for (var i = 0; i < 4; i++) raf.write(0); // Corrupt CRC32 checksum

					raf.seek(localOffset + 18L);
					for (var i = 0; i < 8; i++) raf.write(0); // Compressed size, Uncompressed size

					raf.seek(localOffset + 28L);
					for (var i = 0; i < 2; i++) raf.write(0); // Extra field length

					// Get file name length
					raf.seek(localOffset + 27);
					sb = new StringBuilder();
					sb.append(getB(raf.readByte()));
					raf.seek(localOffset + 26);
					sb.append(getB(raf.readByte()));
					long nameLength = Integer.parseInt(sb.toString(), 16);
					locOff += nameLength;

					raf.seek(localOffset + 26L);
					for (var i = 0; i < 2; i++) raf.write(0); // File name length

					raf.seek(localOffset);
					for (var i = 0; i < 30; i++) // Copy base attributes | + file name (removed) + extra field (empty)
						local.add(raf.readByte());

					// Copy extra field
					long testOff = localOffset + 30 + nameLength;
					a:
					while (true) {
						// Check if it's not another local header
						raf.seek(testOff);
						for (var i = 0; i < 4; i++) {
							if (!isLocal(i, raf.readByte()))
								break;
							if (i == 3)
								break a;
						}
						// Check if it's not starting of central header
						raf.seek(testOff);
						for (var i = 0; i < 4; i++) {
							if (!isCentral(i, raf.readByte()))
								break;
							if (i == 3)
								break a;
						}
						raf.seek(testOff++);
						local.add(raf.readByte());
					}
				}
				check = 0;
			}
			// Update central header offset
			offset = endStart + 1;
			raf.seek(offset + 16);
			byte[] ama = longToBytes(centralOffset - locOff);
			ArrayUtils.reverse(ama);
			ama = Arrays.copyOf(ama, 4);
			for (byte b : ama) raf.write(b);
			// Copy end header
			while (offset < raf.length()) {
				raf.seek(offset++);
				ending.add(raf.readByte());
			}
		}
		try (var fos = new FileOutputStream(outputFile)) {
			var myByteArray = new byte[local.size() + central.size() + ending.size()];
			for (var i = 0; i < local.size(); i++) myByteArray[i] = local.get(i);
			for (var i = 0; i < central.size(); i++) myByteArray[local.size() + i] = central.get(i);
			for (var i = 0; i < ending.size(); i++)
				myByteArray[local.size() + central.size() + i] = ending.get(i);
			fos.write(myByteArray);
		}
		ResourceLib.logger().info("Corrupted archive!");
	}

	private byte[] longToBytes(long x) {
		var buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	private void calcCentralHeader(File file) throws IOException {
		try (var raf = new RandomAccessFile(file, "r")) {
			long offset = raf.length() - 1;
			var check = 0;
			while (offset > 0) {
				raf.seek(offset);
				if (isGood(check, raf.readByte()))
					check++;
				else
					check = 0;
				offset--;
				if (check == 4) {
					endStart = offset;
					var sb = new StringBuilder();
					long bp = offset;
					for (var i = 0; i < 2; i++) {
						raf.seek(offset + 8 + 2);
						sb.append(String.format("%02X", raf.readByte()));
						offset--;
					}
					entries = Long.parseLong(sb.toString(), 16);
					offset = bp;
					sb = new StringBuilder();
					for (var i = 0; i < 4; i++) {
						raf.seek(offset + 16 + 4);
						sb.append(String.format("%02X", raf.readByte()));
						offset--;
					}
					centralOffset = Long.parseLong(sb.toString(), 16);
					return;
				}
			}
		}
	}

	private boolean isGood(int check, byte b) {
		if (check == 0) return b == 0x06;
		if (check == 1) return b == 0x05;
		if (check == 2) return b == 0x4b;
		return b == 0x50;
	}

	private boolean isCentral(int check, byte b) {
		if (check == 0) return b == 0x50;
		if (check == 1) return b == 0x4b;
		if (check == 2) return b == 0x01;
		return b == 0x02;
	}

	private boolean isLocal(int check, byte b) {
		if (check == 0) return b == 0x50;
		if (check == 1) return b == 0x4b;
		if (check == 2) return b == 0x03;
		return b == 0x04;
	}

	private String getB(byte b) {
		return String.format("%02X", b);
	}

}
