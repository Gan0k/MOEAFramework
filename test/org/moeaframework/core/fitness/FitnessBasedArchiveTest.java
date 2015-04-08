//package org.moeaframework.core.fitness;
//
//import jmetal.util.Distance;
//import jmetal.util.archive.Archive;
//import jmetal.util.archive.CrowdingArchive;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.moeaframework.TestUtils;
//import org.moeaframework.core.FitnessEvaluator;
//import org.moeaframework.core.PRNG;
//import org.moeaframework.core.Problem;
//import org.moeaframework.problem.MockRealProblem;
//
//public class FitnessBasedArchiveTest {
//	
//	@Test
//	public void testCrowding() {
//		Problem problem = new MockRealProblem();
//		
//		for (int i = 0; i < 100; i++) {
//			int count = PRNG.nextInt(2, 10);
//			FitnessBasedArchive archive = new FitnessBasedArchive(new CrowdingDistanceFitnessEvaluator(), 100);
//			CrowdingArchive jmetalArchive = new CrowdingArchive(100, 2);
//		
//			for (int j = 0; j < count; j++) {
//				double o1 = PRNG.nextDouble();
//				double o2 = PRNG.nextDouble();
//				
//				archive.add(TestUtils.newSolution(o1, o2));
//				jmetalArchive.add(newJMetalSolution(o1, o2));
//			}
//			
//			archive.update();
//			new Distance().crowdingDistanceAssignment(jmetalArchive, problem.getNumberOfObjectives());
//			
//			compare(archive, jmetalArchive);
//		}
//	}
//	
//	@Test
//	public void testHypervolume() {
//		Problem problem = new MockRealProblem();
//		
//		for (int i = 0; i < 100; i++) {
//			int count = PRNG.nextInt(2, 10);
//			FitnessBasedArchive archive = new FitnessBasedArchive(new HypervolumeContributionFitnessEvaluator(problem, 100.0), 100);
//			HypervolumeArchive jmetalArchive = new HypervolumeArchive(100, 2);
//		
//			for (int j = 0; j < count; j++) {
//				double o1 = PRNG.nextGaussian();
//				double o2 = PRNG.nextGaussian();
//				
//				archive.add(TestUtils.newSolution(o1, o2));
//				jmetalArchive.add(newJMetalSolution(o1, o2));
//			}
//			
//			archive.update();
//			jmetalArchive.actualiseHVContribution();
//			
//			compare(archive, jmetalArchive);
//		}
//	}
//	
//	private jmetal.core.Solution newJMetalSolution(double... values) {
//		jmetal.core.Solution solution = new jmetal.core.Solution(values.length);
//		
//		for (int i = 0; i < values.length; i++) {
//			solution.setObjective(i, values[i]);
//		}
//		
//		return solution;
//		
//	}
//	
//	/**
//	 * Compare the fitness values of both archives.  Note that JMetal stores
//	 * the values in the crowding distance field.  Here we are less concerned
//	 * about having identical values, but ensuring the pairwise comparisons
//	 * are consistent (i.e., scaling does not matter).
//	 * 
//	 * @param archive
//	 * @param jmetalArchive
//	 */
//	private void compare(FitnessBasedArchive archive, Archive jmetalArchive) {
//		Assert.assertEquals(jmetalArchive.size(), archive.size());
//		
//		for (int j = 0; j < archive.size()-1; j++) {
//			for (int k = j+1; k < archive.size(); k++) {
//				double v1 = (Double)archive.get(j).getAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE);
//				double v2 = (Double)archive.get(k).getAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE);
//				double v1j = jmetalArchive.get(j).getCrowdingDistance();
//				double v2j = jmetalArchive.get(k).getCrowdingDistance();
//				
//				System.out.println(v1 + " " + v2 + " / " + v1j + " " + v2j);
//				
//				if (v1j < v2j) {
//					Assert.assertTrue(v1 < v2);
//				} else {
//					Assert.assertTrue(v1 >= v2);
//				}
//			}
//		}
//	}
//
//}
