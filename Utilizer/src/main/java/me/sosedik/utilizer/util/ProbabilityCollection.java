package me.sosedik.utilizer.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.TreeSet;

/**
 * ProbabilityCollection for retrieving random elements based on probability.
 * <br>
 * <br>
 * <b>Selection Algorithm Implementation</b>:
 * <p>
 * <ul>
 * <li>Elements have a "block" of space, sized based on their probability share
 * <li>"Blocks" start from index 1 and end at the total probability of all elements
 * <li>A random number is selected between 1 and the total probability
 * <li>Which "block" the random number falls in is the element that is selected
 * <li>Therefore "block"s with larger probability have a greater chance of being
 * selected than those with smaller probability.
 * </p>
 * </ul>
 *
 * @param <E> Type of elements
 * @author Lewys Davies
 * @version 0.8 (modified to allow nulls)
 */
@NullMarked
public class ProbabilityCollection<E> {

	private final NavigableSet<ProbabilitySetElement<E>> collection = new TreeSet<>(Comparator.comparingInt(ProbabilitySetElement::getIndex));
	private final SplittableRandom random = new SplittableRandom();
	private int totalProbability = 0;

	/**
	 * Gets the number of objects inside the collection
	 *
	 * @return number of objects inside the collection
	 */
	public int size() {
		return this.collection.size();
	}

	/**
	 * Checks collection contains no elements
	 *
	 * @return whether collection contains no elements
	 */
	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	/**
	 * @param object object
	 * @return True if collection contains the object, else False
	 */
	public boolean contains(@Nullable E object) {
		if (object == null) {
			return this.collection.stream()
					.anyMatch(entry -> entry.getObject() == null);
		}
		return this.collection.stream()
				.anyMatch(entry -> object.equals(entry.getObject()));
	}

	/**
	 * @return Iterator over this collection
	 */
	public Iterator<ProbabilitySetElement<E>> iterator() {
		return this.collection.iterator();
	}

	/**
	 * Add an object to this collection
	 *
	 * @param object      object
	 * @param probability share. Must be greater than 0.
	 * @throws IllegalArgumentException if probability <= 0
	 */
	public void add(@Nullable E object, int probability) {
		if (probability <= 0)
			throw new IllegalArgumentException("Probability must be greater than 0");

		ProbabilitySetElement<E> entry = new ProbabilitySetElement<>(object, probability);
		entry.setIndex(this.totalProbability + 1);

		this.collection.add(entry);
		this.totalProbability += probability;
	}

	/**
	 * Remove an object from this collection
	 *
	 * @param object object
	 * @return True if object was removed, else False.
	 */
	public boolean remove(@Nullable E object) {
		Iterator<ProbabilitySetElement<E>> it = this.iterator();
		boolean removed = false;

		while (it.hasNext()) {
			ProbabilitySetElement<E> entry = it.next();
			if (object == null ? entry.getObject() == null : object.equals(entry.getObject())) {
				this.totalProbability -= entry.getProbability();
				it.remove();
				removed = true;
			}
		}

		this.updateIndexes();

		return removed;
	}

	/**
	 * Remove all objects from this collection
	 */
	public void clear() {
		this.collection.clear();
		this.totalProbability = 0;
	}

	/**
	 * Get a random object from this collection, based on probability.
	 *
	 * @return <E> Random object
	 * @throws IllegalStateException if this collection is empty
	 */
	public @Nullable E get() {
		if (this.isEmpty())
			throw new IllegalStateException("Cannot get an object out of a empty collection");

		ProbabilitySetElement<E> toFind = new ProbabilitySetElement<>(null, 0);
		toFind.setIndex(this.random.nextInt(1, this.totalProbability + 1));

		return Objects.requireNonNull(this.collection.floor(toFind)).getObject();
	}

	/**
	 * @return Sum of all element's probability
	 */
	public final int getTotalProbability() {
		return this.totalProbability;
	}

	/*
	 * Calculate the size of all element's "block" of space:
	 * i.e 1-5, 6-10, 11-14, 15, 16
	 *
	 * We then only need to store the start index of each element,
	 * as we make use of the TreeSet#floor
	 */
	private void updateIndexes() {
		int previousIndex = 0;

		for (ProbabilitySetElement<E> entry : this.collection) {
			previousIndex = entry.setIndex(previousIndex + 1) + (entry.getProbability() - 1);
		}
	}

	/**
	 * Used internally to store information about an object's
	 * state in a collection. Specifically, the probability
	 * and index within the collection.
	 * <p>
	 * Indexes refer to the start position of this element's "block" of space.
	 * The space between element "block"s represents their probability of being selected
	 *
	 * @param <T> Type of element
	 * @author Lewys Davies
	 */
	static final class ProbabilitySetElement<T> {
		private final @Nullable T object;
		private final int probability;
		private int index;

		/**
		 * @param object      object
		 * @param probability probability
		 */
		private ProbabilitySetElement(@Nullable T object, int probability) {
			this.object = object;
			this.probability = probability;
		}

		/**
		 * @return <T> The actual object
		 */
		public @Nullable T getObject() {
			return this.object;
		}

		/**
		 * @return Probability share in this collection
		 */
		public int getProbability() {
			return this.probability;
		}

		// Used internally, see this class's documentation
		private int getIndex() {
			return this.index;
		}

		// Used Internally, see this class's documentation
		private int setIndex(int index) {
			this.index = index;
			return this.index;
		}

	}

}
