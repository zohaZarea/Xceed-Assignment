
package mytwitter;

// twitter API libraries
import java.util.List;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory; 
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.User;
import java.util.Comparator;

//MongoDB libraries
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bson.Document;
import twitter4j.UserMentionEntity;



public class MyTwitter {

   
    public static void main(String[] args) throws TwitterException {
        
     
        // building twitter API
         ConfigurationBuilder cb = new ConfigurationBuilder();
         
         MongoDatabase db ;
   
         cb.setDebugEnabled(true)
              .setOAuthConsumerKey("OvIgOJEiIgfgu97B0sIRmW4I3")
              .setOAuthConsumerSecret("6TVhvugNlskrCDvjTeQvM9uTngJTf1sHco66ziVBMxa8YzE123")
              .setOAuthAccessToken("3494043506-Hp9t9IlrooQJN2ArHZYu15lsimwg5RzywhmbUFt")
              .setOAuthAccessTokenSecret("fHUVxdGNdX2zVAYrzTgsr6hHKaT8CsvCQOM0uFYESc4Ij");
   
      TwitterFactory tf = new TwitterFactory(cb.build());
   
      twitter4j.Twitter tw = tf.getInstance();
   
    // reading twitter timeline
      
   //to read 100 tweets
Paging paging = new Paging();
paging.setCount(100);
      
List <Status> statuses = tw.getHomeTimeline(paging);

 
   // count 100 tweets (from 0 to 99)
   int i =0;
   
    try
    {
      // connect to MongoDB
      MongoClient mongo = new MongoClient( "localhost" , 27017 );
      db = mongo.getDatabase("test");
     
      System.out.println("Connected");
      
     

     // create table in MongoDB .. table name is "tweets"
          MongoCollection<Document> table = db.getCollection("tweets");
          
	/**** Insert ****/
	// create many documents to store name and tweet
        List<Document> document = new ArrayList<Document>();
        
          // variables to store name and tweet from twitter timeline 
          String name;
          String tweet;
          int retweets_counter=0;
          int followed_counter=0;
        
          // array of statuses (used to get TOP 5 retweets)
           Status Statuses_array[] = new Status[100];
       
           // print tweets and insert into MongoDB
         for (Status status1 : statuses)
         {
         name = status1.getUser().getName() ;
         tweet = status1.getText();
         
         retweets_counter = status1.getRetweetCount();
         followed_counter = status1.getUser().getFollowersCount() ;
          
          //print tweets
         System.out.println(name);
         System.out.println(tweet);
         System.out.println("Retweeted "+ retweets_counter + " times");
         System.out.println("This user has  "+ followed_counter + " followers");
         
         
      // store status in the array
       Statuses_array[i] = status1;
         
       // "mentions" is an array of mentioned accounts in the status
       UserMentionEntity[] mentions =status1.getUserMentionEntities();

       // print names of mentioned accounts
for(int m=0 ; m<mentions.length ; m++)
{
System.out.println( "mentioned account: " + mentions[m].getScreenName() + " "); 
} // end for

       
       
         
         // numbering of tweets
         System.out.println("Tweet #" + i++);
         
     
         
         // to separate each tweet from the other
         System.out.println("---------");
          
          
         // insert tweet to the document
       document.add(new Document("Numbering", i));
       document.add(new Document("Name", name));
       document.add(new Document("Tweet", tweet));
       
      } // end for loop
         
         // insert all tweets into table in MongoDB
      table.insertMany(document);
     
      System.out.print(i++) ;
      System.out.println(" tweets were inserted into MongoDB");
     System.out.println("--------------------------------------------------\n\n");
     
     
       // sort Statuses_array according to Retweet count 
      Arrays.sort(Statuses_array, new Comparator<Status>() {
        @Override
        public int compare(Status s1, Status s2) {
            return 
             s1.getRetweetCount()- s2.getRetweetCount();
        }}); 
      
      
      // Store TOP 5 Retweets in top5 array
Status[] top5_retweeted = Arrays.copyOfRange(Statuses_array, Statuses_array.length-5,Statuses_array.length);
 

// print TOP 5 Retweets
System.out.println("********* TOP 5 RETWEETS *********");
System.out.println("Top 1 Tweet is:\n " + top5_retweeted[4].getText() + "\n it was retweeted " + top5_retweeted[4].getRetweetCount()+ " times \n") ;
System.out.println("Top 2 Tweet is:\n " + top5_retweeted[3].getText() + "\n it was retweeted " + top5_retweeted[3].getRetweetCount()+ " times \n") ; 
System.out.println("Top 3 Tweet is:\n " + top5_retweeted[2].getText() + "\n it was retweeted " + top5_retweeted[2].getRetweetCount()+ " times \n") ;
System.out.println("Top 4 Tweet is:\n " + top5_retweeted[1].getText() + "\n it was retweeted " + top5_retweeted[1].getRetweetCount()+ " times \n") ;
System.out.println("Top 5 Tweet is:\n " + top5_retweeted[0].getText() + "\n it was retweeted " + top5_retweeted[0].getRetweetCount()+ " times \n") ;

 
  System.out.println("\n\n");

    
    // get username from status object and store it in users array
      User users[] = new User[100]; // Array of twitter usernames (includes duplicates)
      for (int y=0 ; y<100 ; y++) {
                 users[y] = Statuses_array[y].getUser() ;
                  } // end for

// remove duplicate usernames to check which username has the most followers
     List<User> list = Arrays.asList(users);
    Set<User> set = new HashSet<User>(list);
    User[] users_without_duplicates = new User[set.size()];
    set.toArray(users_without_duplicates);
    
   // sort "users_without_duplicates" array according to followers count
      Arrays.sort(users_without_duplicates, new Comparator<User>() {
        @Override
        public int compare(User s1, User s2) {
            return 
             s1.getFollowersCount()- s2.getFollowersCount();
        }}); 
                 // Store TOP 5 followed in top5_followed array
                User[] top5_followed = Arrays.copyOfRange(users_without_duplicates, users_without_duplicates.length-5,users_without_duplicates.length);
           
           
                

      // print TOP 5 Followed
System.out.println("********* TOP 5 FOLLOWED *********");
System.out.println("Top 1 followed account is: " + top5_followed[4].getName() + "\n it has " + top5_followed[4].getFollowersCount()+ " followers \n") ;
System.out.println("Top 2 followed account is: " + top5_followed[3].getName() + "\n it has " + top5_followed[3].getFollowersCount()+ " followers \n") ; 
System.out.println("Top 3 followed account is: " + top5_followed[2].getName() + "\n it has " + top5_followed[2].getFollowersCount() +" followers \n") ; 
System.out.println("Top 4 followed account is: " + top5_followed[1].getName() + "\n it has " + top5_followed[1].getFollowersCount()+ " followers \n") ; 
System.out.println("Top 5 followed account is: " + top5_followed[0].getName() + "\n it has " + top5_followed[0].getFollowersCount()+ " followers \n") ; 

    } // end try
    
    catch (Exception e) {
	e.printStackTrace();
    }  // end catch
  
    } // end main

} // end class
