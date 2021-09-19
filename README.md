# README
To build use:
`mvn package`

To run use:
`./run`

This will give you a barebones REPL, where you can enter text and you will be output at most 5 suggestions sorted alphabetically.

To start the server use:
`./run --gui [--port=<port>]`

--------------

Project Details: 
- Onboarding Project
- nmasi

Questions: N/A

Design Choices: N/A

Errors/Bugs:
- Detection to prevent the star from which we're looking at everything
else relative to from being added to the list of nearest stars may be
slightly flawed in preventing different stars at that same origin location
from being added too
- The implementation of randomness when choosing between stars at the same
location may not be perfectly random
- error handling not fully complete

Tests:
- base testing suite