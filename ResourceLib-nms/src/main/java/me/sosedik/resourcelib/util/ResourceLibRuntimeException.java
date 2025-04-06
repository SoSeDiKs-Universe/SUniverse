package me.sosedik.resourcelib.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ResourceLibRuntimeException extends RuntimeException {

	public ResourceLibRuntimeException(String message) {
		super(message);
	}

}
