# template-assignment-handout

Changes I made in BasicKlondike:
-I changed getRankValue(), isOneRankLower(), and
isOppositeColor() from private to protected
to allow WhiteheadKlondike to use the helper methods
without having code duplication.

-I fixed the format of the klondike deck to display Draw
above Foundation and added commas, so the tests
are able to recognize the cards.

-Lastly, I removed the cardIndex variable in 
startGame method as it was not being used, and
causing errors.

New classes:

-I added WhiteheadKlondike in the klondike.model.hw04 package.
It basically extends the BasicKlondike in order ot implement
the new Whitehead rules. I overrid startGame(), movePile(),
and moveDraw(), and made sure to change them based
on specificaitons in the assingment. I also added
main method to test how the game works.

-Then I created KlondikeCreator in the klondike.model.hw04 pacakge.
I basically made a factory class with gameType enum and created
static methods to return the right model instance.

-Lastly, I have Klondike in the klondike package
as the main class that has command line argument parsing,
which also supports the game variation selection 
and the pile/draw card counting.

Testing:

-I created WhiteheadKlondikeTest which cover the four
Whitehead rules differences comapred to BasicKlondike.

-I created KlondikeCreatorTest which checks if the factory
class creates the correct game instances.

-I created BasicKlondikeTest which tests valid operation and exceptions.