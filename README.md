cdevoto-projects Repository
===========================

This is a repository for various personal projects that I am working on. 

Projects
========


1. Einstein's Riddle
====================
A Drools implementation of Einstein's Riddle used to test the performance and scalability of the Drools rule engine.  This is an Eclipse project created using Drools 5.2.0, and it assumes that you have the Eclipse
Drools Plug-in installed.

As I was ramping up on some of the technologies that we are using for
one of our projects, I decided to test the scalability/performance of
the Drools rules engine and also to familiarize myself with the engine
by programming it to solve Einstein's Riddle (see below). 

For my first attempt, I tried to solve the puzzle by feeding the system
with all possible permutations of Person objects (15,625) as facts and
then applying all of the rules in a single pass.  This attempt failed
miserably-- I overflowed the heap after inserting about 6300 facts into
the Rete graph. 

For my second attempt, I tried to solve the problem in two passes.  In
the first pass, I again fed the system with all possible permuations of
the Person object, but this time, I only applied the subset of rules
that allowed for the direct pruning of invalid Person object
permutations (rules 1, 2, 3, 4, 5, 6, 7, 8, 9, 12, 13, and 14 below).
This first pass reduced the number of Person object permuations the
1216.  The outputs of the first pass were used as inputs to the second
pass, which applied all of the remaining rules (rules 4, 10, 11, 15, and
16 below).  This second attempt succeeded in producing a solution after
about 25 seconds with a maximum heap size of about 90MB.


CONCLUSIONS:
------------
1) it is fairly easy to overflow the heap with Drools unless you take
proper precautions to control the complexity of the rule set as well as
the number of facts in working memory by splitting the work across
multiple sessions and rule sets. 
2) The performance was less than stellar, so we will need to keep an eye
on this as our system evolves.

For those who are interested, I've attached a ZIP file containing my
Eclipse project for solving Einstein's Riddle using Drools. 


EINSTEIN'S RIDDLE:
------------------
It is often called Einstein's Puzzle or Einstein's Riddle because it is
said to have been invented by Albert Einstein as a boy; it is sometimes
claimed that only 2% of the population can solve it.

1. The British person lives in the red house.
2. The Swede keeps dogs as pets.
3. The Dane drinks tea.
4. The green house is on the left of the white house.
5. The green homeowner drinks coffee.
6. The man who smokes Pall Mall keeps birds.
7. The owner of the yellow house smokes Dunhill.
8. The man living in the center house drinks milk.
9. The Norwegian lives in the first house.
10. The man who smokes Blend lives next to the one who keeps cats.
11. The man who keeps the horse lives next to the man who smokes Dunhill.
12. The man who smokes Bluemaster drinks beer.
13. The German smokes Prince.
14. The Norwegian lives next to the blue house.
15. The man who smokes Blend has a neighbor who drinks water.
16. Each of the five houses is painted a different color, and their
inhabitants are of different national extractions, own different pets,
drink different beverages and smoke different brands of cigarettes.

Question: Who owns the fish?
