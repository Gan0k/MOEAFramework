package org.moeaframework.core.fitness;

import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Maintains a non-dominated archive of solutions with a maximum capacity.
 * If the size exceeds the capacity, one or more solutions are pruned based on
 * the fitness calculation.  The fitness calculation only occurs when the
 * addition of a solution exceeds the capacity.  The fitness can be manually
 * calculated by calling {@link #update()}.
 */
public class FitnessBasedArchive extends NondominatedPopulation {
	
	/**
	 * The maximum capacity of this archive.
	 */
	private final int capacity;
	
	/**
	 * The fitness evaluator for computing the fitness of solutions.
	 */
	private final FitnessEvaluator fitnessEvaluator;
	
	/**
	 * The fitness comparator for comparing fitness values.
	 */
	private final FitnessComparator fitnessComparator;

	/**
	 * Constructs an empty fitness-based archive.
	 * 
	 * @param evaluator the fitness evaluator for computing the fitness of
	 *        solutions
	 * @param capacity the maximum capacity of this archive
	 */
	public FitnessBasedArchive(FitnessEvaluator evaluator, int capacity) {
		this(evaluator, capacity, new ParetoDominanceComparator());
	}

	/**
	 * Constructs an empty fitness-based archive.
	 * 
	 * @param evaluator the fitness evaluator for computing the fitness of
	 *        solutions
	 * @param capacity the maximum capacity of this archive
	 * @param comparator the dominance comparator
	 */
	public FitnessBasedArchive(FitnessEvaluator evaluator, int capacity,
			DominanceComparator comparator) {
		super(comparator);
		this.fitnessEvaluator = evaluator;
		this.capacity = capacity;
		
		fitnessComparator = new FitnessComparator(evaluator.areLargerValuesPreferred());
	}

	/**
	 * Constructs a fitness-based archive initialized with the specified
	 * solutions.
	 * 
	 * @param evaluator the fitness evaluator for computing the fitness of
	 *        solutions
	 * @param capacity the maximum capacity of this archive
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public FitnessBasedArchive(FitnessEvaluator evaluator, int capacity,
			DominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		this(evaluator, capacity, comparator);
		addAll(iterable);
	}

	/**
	 * Constructs a fitness-based archive initialized with the specified
	 * solutions.
	 * 
	 * @param evaluator the fitness evaluator for computing the fitness of
	 *        solutions
	 * @param capacity the maximum capacity of this archive
	 * @param iterable the solutions used to initialize this population
	 */
	public FitnessBasedArchive(FitnessEvaluator evaluator, int capacity,
			Iterable<? extends Solution> iterable) {
		this(evaluator, capacity, new ParetoDominanceComparator(), iterable);
	}

	@Override
	public boolean add(Solution solution) {
		boolean solutionAdded = super.add(solution);
		
		if (solutionAdded) {
			if (size() > capacity) {
				update();
				truncate(capacity, fitnessComparator);
			}
		}
		
		return solutionAdded;
	}

	/**
	 * Updates the fitness of all solutions in this population.
	 */
	public void update() {
		fitnessEvaluator.evaluate(this);
	}

}
