package einstein.riddle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class BasicRulesApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
		List<Object> facts = generatePermutations();
		facts = processRules(facts, "prune-person-rules.drl");
        facts = processRules(facts, "prune-solution-rules.drl");
        if (facts.size() == 0) {
        	System.out.println("No solution found.");
        } else if (facts.size() > 1) {
        	System.out.println("Multiple solutions found.");
        	int i = 1;
        	for (Object fact : facts) {
        		System.out.println((i++) + " SOLUTION: " + fact);
        		System.out.println("\tThe " + ((Solution) fact).getFishOwner().getNationality() + " owns the fish.");
        	}
        } else {
        	Solution fact = (Solution) facts.get(0);
            System.out.println("SOLUTION: " + fact);
    		System.out.println("\tThe " + fact.getFishOwner().getNationality() + " owns the fish.");
        }
        System.out.println("Time Elapsed: " + (System.currentTimeMillis() - start) + " ms.");
	}

	private static List<Object> processRules(List<? extends Object> facts,
			String classPathResource) {
		System.out.println("Total facts: " + facts.size());
		List<Object> output = new ArrayList<Object>();
		KnowledgeBase knowledgeBase = createKnowledgeBase(classPathResource);
		StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
		try {
			//int i = 1;
			for (Object fact : facts) {
			    //System.out.println((i++) + ". Inserting  " + fact + " " + (Runtime.getRuntime().freeMemory() / (1024 * 1024)));
			    session.insert(fact);
			}
			session.fireAllRules(); // match every fact in the session with every rule condition to see if all of the rule's conditions can be satisfied; if so, execute the rule's consequent.
			output.addAll(session.getObjects());
			//System.out.println(Runtime.getRuntime().totalMemory() / (1024 * 1024));
			return output;
		} finally {
			session.dispose();
		}
	}

	private static List<Object> generatePermutations() {
		List<Object> permutations = new LinkedList<Object>();
		for (Nationality nationality : Nationality.ALL) {
			for (HouseColor houseColor : HouseColor.ALL) {
				for (int i = 1; i <= 5; i++) {
					for (Pet pet : Pet.ALL) {
						for (Drink drink : Drink.ALL) {
							for (Smoke smoke : Smoke.ALL) {
								Person person = new Person();
								person.setNationality(nationality);
								person.setHouseColor(houseColor);
								person.setHousePosition(i);
								person.setPet(pet);
								person.setDrink(drink);
								person.setSmoke(smoke);
								permutations.add(person);
							}
						}
					}
				}
			}
		}
		return permutations;
	}

	private static KnowledgeBase createKnowledgeBase(String classPathResource) {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder.add(ResourceFactory.newClassPathResource(classPathResource), ResourceType.DRL);
		
		if (builder.hasErrors()) {
			throw new RuntimeException(builder.getErrors().toString());
		}
		
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());
		return knowledgeBase;
	}

}
