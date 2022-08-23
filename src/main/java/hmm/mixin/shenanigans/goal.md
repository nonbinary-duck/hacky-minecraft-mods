- Make it possible to get creative blocks
- Create a fake information data-race-like behaviour where a falling sand can take on the properties of a different block
- Then, make it possible to combine two falling blocks into a third block
    - Probably just if they get hit by a piston or something
        - The general falling block glitch should be the complicated part
    - My other idea was to have it so when two falling blocks in different threads land, they combine to make another
        - This seems less satisfying, though more accurate


## Data-Race Component
- Sand needs to start falling but another block needs to be in its place "at the same time"
    - In 1.12 this was possible with async behaviour
- Implement in a way where an action can cascade and allow an artificial version of the funky behaviour
    - Action could be glass being used to update rails, that update then pushing both of the pistons

## Falling-block recipies
- Ideally a random combination of two blocks
    - Get any block as a falling block, including chests and stuff
    - Maybe require a third block which is always the same, something like a grass block
- When both blocks pushed in the same tick by the same slime block when they're both at the same exact position