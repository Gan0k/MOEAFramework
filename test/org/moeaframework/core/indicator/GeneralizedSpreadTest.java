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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link GeneralizedSpread} class.
 */
public class GeneralizedSpreadTest {

	/**
	 * Tests if an exception is thrown when using an empty reference set.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyReferenceSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		NondominatedPopulation approximationSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");

		GeneralizedSpread gs = new GeneralizedSpread(problem, referenceSet);
		gs.evaluate(approximationSet);
	}

	/**
	 * Tests if an empty approximation set returns a value of zero.
	 */
	@Test
	public void testEmptyApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		GeneralizedSpread gs = new GeneralizedSpread(problem, referenceSet);
		Assert.assertEquals(1.0, gs.evaluate(approximationSet),
				Settings.EPS);
	}

	/**
	 * Tests if infeasible solutions are properly ignored.
	 */
	@Test
	public void testInfeasibleApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("CF1");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("CF1");
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		Solution solution = problem.newSolution();
		solution.setObjectives(new double[] { 0.5, 0.5 });
		solution.setConstraints(new double[] { 10.0 });
		approximationSet.add(solution);

		GeneralizedSpread gs = new GeneralizedSpread(problem, referenceSet);
		Assert.assertEquals(1.0, gs.evaluate(approximationSet),
				Settings.EPS);
	}

	/**
	 * Runs through some simple cases to ensure the spacing metric is
	 * computed correctly.
	 */
	@Test
	public void testSimple() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");

		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));

		NondominatedPopulation approximationSet = new NondominatedPopulation();

		GeneralizedSpread gs = new GeneralizedSpread(problem, referenceSet);

		Assert.assertEquals(1.0, gs.evaluate(approximationSet), Settings.EPS);

		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(1.0, gs.evaluate(approximationSet), Settings.EPS);

		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertEquals(0.0, gs.evaluate(approximationSet), Settings.EPS);

		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertEquals(0.0, gs.evaluate(approximationSet), Settings.EPS);

		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 1.0));
		approximationSet.add(TestUtils.newSolution(0.25, 0.75));
		approximationSet.add(TestUtils.newSolution(1.0, 0.0));
		Assert.assertTrue(gs.evaluate(approximationSet) > 0.0);
	}

}
