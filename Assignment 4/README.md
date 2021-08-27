# Assignment 4

## Part 1: Theory  
Question 1-5 are in a separate file named question1-5.pdf

## Part 2: Programming

In general, our search engine can search by ID, by name (whole/part), by region (whole/part),  
by type (whole/part), by coordinates (at/within). The search ignores upper case/ lower case, if  
you enter “ABC” or “abc” and the data has “aBc” or “Abc”, it still matches and returns the right  
answers.  
Besides, we can print the in-memory indices (BST and inverted index) in human-readable
format.  

#### Main ADTs implemented

The **hash map** is built and used to support the implementation of inverted index and  
SearchIndex

The **balanced BST** is implemented using AVLTree algorithm. The tree will be rebalanced after
each insertion (if needed). If the tree is right-heavy, it will be left-rotated. If the tree is left-heavy,
it will be right-rotated.
The minimum height BST will be created when we sort a list of documents before putting them
into the BST. Professor Desai instructed that with a sorted list, we put the middle element of
the list into the root, then put the middle element of the left half into the left child and the
middle element of the right half into the right child, doing them recursively we will have the
minimum height BST. However, here with the AVLTree, with the rebalance mechanism, when we
insert the sorted list from the start to the end, the minimum height BST is automatically
created.
Printing of the BST: for the BST with more than 120,000 nodes, we chose to print it under this
format: the root node is printed first with (key, value), then we print its left child and right child,
then we print left/right child of each children, etc. In other words, we print the nodes with inorder
traverse.

Usage of the BST: the BST is used to search by the id and find the results of searches of
inverted indices and B+ trees.

The **inverted index** is used to search by the GeoName, GeoRegion, GeoType.

* The GeoName is the name of a place.
* The GeoRegion is equivalent to the column “Location” in the input file, for example the ID “ERHOK” has the GeoRegion “Rivière-Bonjour; La Matanie”.
* The GeoType is equivalent to the column “Generic Term” in the input file, for example the ID “EZAAH” has the GeoType “National Historic Site”.

6 inverted indices are used in our search, 2 for search by name, 2 for search by region, and 2 for search by type.  

Printing of the inverted index: because the input file has more than 120,000 records, we chose to print the indices with the following format: print each term with its associated IDs.

-----------------------------------------------------------------------------------------
**Format of lines in the script file and QUERY FORMAT**  
There are 2 kinds of lines in the script file: QUERY line and PRINT line

**QUERY lines**  
The QUERY line is used when you want to search information in the input file. There are some
kinds of QUERIES.

**QUERY of names**  
General format: “QUERY name: kind_of_match(S)”  
 \__(a)__/ \_(b)_/ \_____(c)___/ \ (d) / 
 
(a) says that this is a QUERY  
(b) expresses the field: here it’s the field “name”  
(c) describes the kind of match, it could be EXACT_MATCH/ MATCH_ALL/ MATCH_ANY or nothing  
(d) is the string S, it could be a single string or many substrings depending on the kind of match  

There are 4 kinds of QUERY of names:

* **EXACT_MATCH**  
Format: “QUERY name: EXACT_MATCH(S)”  
In this query, S is the whole string of the GeoName. The search will return the document/s with the GeoName 
that matches exactly the string S.  
For example, if we give the query “QUERY
name: EXACT_MATCH(Lac verte)”, the search will return the documents whose name is exactly
“Lac verte”

* **MATCH_ALL**  
Format: “QUERY name: MATCH_ALL(S1 S2 … Sn)”
In this query, S1, S2, … ,Sn are substrings of the geographical names. The search will return
the document/s with the GeoName that contains ALL of these substrings.  
For example, when we give the query “QUERY name: MATCH_ALL(Lac verte)”, the search will return all documents
with names containing “Lac” AND “verte”

* **MATCH_ANY**  
Format: “QUERY name: MATCH_ANY(S1 | S2 | … | Sn)”
In this query, S1, S2, … ,Sn are substrings of the geographical names.
The search will return the document/s with the GeoName that contains at least 1 of these
substrings.  
For example, when we give the query “QUERY name: MATCH_ANY(Lac verte |
national)”, the search will return documents with names containing “Lac verte” OR “national”

* **Default query by name**  
If you don’t mention EXACT_MATCH/ MATCH_ALL/ MATCH_ANY in your query, the search will
return the result of MATCH_ALL.  
For example, for query “QUERY name: Lac verte”, the search will return documents whose
names containing “Lac” AND “verte"

**QUERY of regions**  
QUERY of regions applies the same rules as ‘QUERY of names’ with EXACT_MATCH/
MATCH_ALL/ MATCH_ANY/ default except the field name will be “region”.  
* Examples:  
QUERY region: EXACT_MATCH(mont-royal)  
QUERY region: MATCH_ALL(Chertsey Matawinie)  
QUERY region: MATCH_ANY(Amos | Matawinie)  
QUERY region: (mont-royal)  

**QUERY of types**  
QUERY of types applies the same rules as ‘QUERY of names’ with EXACT_MATCH/
MATCH_ALL/ MATCH_ANY/ default except the field name will be “type”.  
* Examples:  
QUERY type: EXACT_MATCH(national park )  
QUERY type: MATCH_ALL(national park)  
QUERY type: MATCH_ANY(national park | park)  
QUERY type: national park  

**QUERY of locations**  
There are 2 kinds of QUERY of locations  

* AT  
Format: “QUERY location: AT(latitude, longitude)”  
In this query, the search will return the documents with location AT the given coordinates  

* WITHIN  
Format: “QUERY location: WITHIN(fromLatitude, fromLongitude | toLatitude, toLongitude)  
In this query, the search will return the documents with location BETWEEN the two given
coordinates.  

**QUERY of ID**  
Format: “QUERY id: S” in which S is the string of GEOID  
Example: “QUERY id: EAAAF”  

**QUERY of multiple fields**  
QUERY of multiple fields is combined by clauses of single field.  
Format: “**QUERY** clause_1 **AND** clause_2 **AND** … **AND** clause_N”  
In this format, clause_n is defined as follows: clause_n = “field_name: field_value”  
Field names can be: “id”, “name”, “region”, “type”, “location”  
Field values depend on the field name, the rules are as follows:  
For “id” field, field_value = S, with S: string of GEOID  
For “name”, “region”, “type” field, field_value = EXACT_MATCH/ MATCH_ALL/ MATCH_ANY/  
empty_string(S), with S is a whole string/ substrings of the field_name.  
For “location”, field_value = AT/WITHIN(S), with S is a string of coordinates  

Basically, the format of clause_n of field “field_name” is exactly the format of the “QUERY of
field_name” without the word “QUERY”

* Examples:  
**QUERY** name: EXACT_MATCH(Lac verte) **AND** type: lake  
The search will return the documents with GeoName “Lac verte” and type “lake”  
**QUERY** type: lake **AND** region: Baie-d'Hudson,  
The search will return the documents with type “lake” and in region “Baie-d'Hudson”  
**QUERY** type: lake **AND** region: Baie-d'Hudson AND location: WITHIN(55.001N,74.5W |
55.8N,76.10W)  
The search will return documents with type “lake” and in region “Baie-d'Hudson” and have
locations between (55.001N,74.5W | 55.8N,76.10W)

**PRINT commands**  
The PRINT command is used when you want to print information in the in-memory indices. The
sample command script file was set up with 4 PRINT lines at the end of the file. You need to
uncomment them to run (as they generate large output):  

* “PRINT id”, this command is to print the BST with keys of ids.  
* “PRINT name”, this command is to print the inverted index of names.  
* “PRINT type”, this command is to print the inverted index of types.  
* “PRINT region”, this command is to print the inverted index of regions.  

#### Instructions on running the program
Execute the main method in the class Driver with the following syntax (after compiling it):  
java Driver cgn_qc_csv_eng.csv script.txt result.txt  
All the results of the program will be output to the file result.txt  
