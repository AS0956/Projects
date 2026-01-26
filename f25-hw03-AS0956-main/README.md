# Assignment 3 - Easy Come, Easy Go: Controller

Start by copying your entire src folder from HW2 to this project.
Then follow the instructions here: [https://northeastern.instructure.com/courses/225692/assignments/2882047](https://northeastern.instructure.com/courses/225692/assignments/2882047)

I improved my KlondikeTextualView to have an Appendable field, and a seconds constructor which
accepts both a model an Appendable. I also added null-checking validation. Furthermore, I added
render() and renderMessage() methods which write to the Appendable with the right IOException
handling.

I also fixed my TextualView interface where I added void render() method which renders
the current game state by writing the model's string
representation to the output destination.
