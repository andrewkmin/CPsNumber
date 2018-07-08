# CPsNumber
The NBA-equivalent of the Kevin Bacon number.

User manual is available under the file Manual.pdf.

Our project is called the Chris Paul Number and is an implementation project.

We took the idea of the Kevin Bacon Number and decided to implement it for the NBA. We
used the data from basketball-reference.com to get a list of all the players who have
played in the NBA in the last 3 years and entered the league before 2009. Using this information, we then create a graph where an edge is placed
between two players if they have played on the same team. We then do BFS on the input
player1 and return the distance it took to reach player2. Our implementation also returns
the number of players that are numHops away from player1, the maximum distance required
to reach all nodes from player1, and the path that was taken from player1 to player2.
Running our project will take around 2 and a half minutes because of the time required to 
scrape data.

We used the categories graph and graph algorithms, social networks, information networks,
and document search. We created a graph of our information and used BFS to find the 
information we desired. The concept of the Chris Paul Number comes from social networks 
in the real world in which we can create a network of teammates in the NBA. We used
information networks by pulling our information from a website on the world wide web.
And finally, we used document search by scraping the data we were looking for from each
players page of information.

We named our project the Chris Paul Number because it turns out that of the nodes (players) that we considered, there are 5 players that are tied for the lowest value of 2 for maxDistance. In other words, Raymond Felton, Luc Mbah a Moute, DeAndre Jordan, Chris Paul, and Mareese Speights all have a maxDistance of 2, meaning amongst the players we are considering in our graph, every other player is at most 2 hops away. Interestingly, each of these 5 players have played (or are currently playing) for the Los Angeles Clippers in the past 3 years, potentially implying that the Clippers have a high turnover rate. This intuitively makes sense, given that the Clippers have made a few "blockbuster" deals recently (aka significant trades involving many players or a player of high caliber). This also makes for an interesting topic for further research.

Work Breakdown:

Danny: I cleaned up our implementation by removing an edge case (Josh Childress) and
ensuring the output is clean looking and clear. Also, I added the functionality of 
recording the path taken from player1 to player2 and outputting the result. Along with 
this, I wrote up the user manual and the summary above.

Nathan: I constructed the graph of players from scratch by parsing through data retrieved from basketball-reference.com using RegEx. Retrieving all NBA players within a ~20 year period would have required a significant amount of time to do, so I subjected the players to criteria such as whether or not they appeared in the NBA before 2009, as well as whether or not they've been active in the past 3 years. This resulted in a significantly better run time for constructing the graph as well as later performing BFS. Additionally, in the construction of the graph, I added an optimization by storing the rosters of teams the program had already come across so that the program would not have to make duplicate calls to the same webpage. 

Andrew: First, I implemented the BFS algorithm. Then, I added the the functionality for computing the max distance between a given player and any other player in the graph as well as the number of "hops" between two nodes, which in our case represent NBA players. I also added a "pruning" function to remove a player from his own teammate set (note: a player was added to his own teammate set as part of the optimization added by Nathan, mentioned above). 