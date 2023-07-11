# Information Retrieval
Song Search Engine App - Navigate the InformationRetrievalReport.pdf which is a short explanation of the system with pictures (greek language)


#### About this app
1) This is a web application that is a search engine for songs. It is very easy to use as it's UI is very similar with google's. It supports incognito search, voice recognization
and boolean queries. Furthermore it supports semantic retrieval and in each case it presents the top songs to the user. The user can click on one of the retrieved songs and read
it's lyrics.<br /><br />
2) The songs where downloaded from Kaggle and were indexed using Lucene library. The retrieval of the songs is also supported by Lucene's APIs. In general, the web application is
built with Java, HTML, Javascript and Spring Boot. For the semantic retrieval (using machine learning) the ndj4 library was used. <br /><br />
3) In order to run the this web application:<br />  i) Download the source code from this repository.<br />   ii) Open Eclipse or InteliJ (InteliJ is prefered becouse in Eclipse you will
probably need extra configuration).<br />  iii) Import 'inforet' folder as a Maven project.<br />  iv) Run the sql script you will find under the recourses/DDL folder in order to create the
necessary table to your database.<br />   v) Change username/password from properties file to your MySQL account information.<br /> vi) Download glove.6B.zip from https://nlp.stanford.edu/projects/glove/ and put glove.6B.50d pretrained model under the recourses/DDL folder.<br />  vii) Execute the filter.py programm under the recourses/DDL folder and now you have your model with word2vec ready.<br /> viii) You are good to go! Fill the comments of the code with your paths, execute the programm and go to localhost to navigate the application.
