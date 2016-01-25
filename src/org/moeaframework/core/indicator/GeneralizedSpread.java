/* Copyright 2009-2016 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.indicator;

import org.apache.commons.math3.stat.StatUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.ObjectiveComparator;
import org.moeaframework.core.comparator.LexicographicalComparator;
import org.moeaframework.core.Problem;

/**
 * Generalized Spread metric.
 */
public class GeneralizedSpread extends NormalizedIndicator {

	/**
	 * Constructs a maximum Pareto front error evaluator for the specified
	 * problem and corresponding reference set.
	 *
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public GeneralizedSpread(Problem problem,
			NondominatedPopulation referenceSet) {
		super(problem, referenceSet);
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet),
				getNormalizedReferenceSet());
	}

	/**
	 * Computes the maximum Pareto front error for the specified problem given
	 * an approximation set and reference set. While not necessary, the
	 * approximation and reference sets should be normalized. Returns
	 * {@code Double.POSITIVE_INFINITY} if the approximation set is empty.
	 *
	 * @param problem the problem
	 * @param approximationSet an approximation set for the problem
	 * @param referenceSet the reference set for the problem
	 * @return the generational distance for the specified problem given an
	 *         approximation set and reference set
	 */
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet,
			NondominatedPopulation referenceSet) {

		if (approximationSet.isEmpty()) {
			return 1.0;
		}

		int numberOfObjectives = approximationSet.get(0).getNumberOfObjectives();

		Solution[] extremeValues = new Solution[numberOfObjectives];
		for (int i = 0; i < numberOfObjectives; i++) {
			referenceSet.sort(new ObjectiveComparator(i));
			extremeValues[i] = referenceSet.get(referenceSet.size()-1);
		}

		approximationSet.sort(new LexicographicalComparator());

		if (IndicatorUtils.euclideanDistance(problem,approximationSet.get(0),
			approximationSet.get(approximationSet.size()-1)) == 0.0) {

			return 1.0;

		} else {

			double[] distances = new double[approximationSet.size()];

			for (int i = 0; i < approximationSet.size(); i++) {
				distances[i] = IndicatorUtils.distanceToNearestNeighbour(problem, i,
						approximationSet);
			}

			double meanDistance = StatUtils.mean(distances);

			double distExtremes = 0.0;
			for (int i = 0 ; i < extremeValues.length; i++) {
				distExtremes += IndicatorUtils.distanceToNearestSolution(problem,
						extremeValues[i], approximationSet);
			}

			double normalizedSum = 0.0;
			for (int i = 0; i < approximationSet.size(); i++) {
				normalizedSum += Math.abs(distances[i] - meanDistance);
			}

			return (distExtremes + normalizedSum) /
				(distExtremes + (approximationSet.size()*meanDistance));
		}
	}
}

