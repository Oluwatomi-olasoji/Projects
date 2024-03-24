#include <iostream>
using namespace std;

//Guessing game 
 int main(){
 	int secretnum= 7;
 	int input;
 	char input2;
 	int guesscount= 0;
 	int guesslimit= 7;
 	
 	cout<< "Welcome to Tomi's guessing game!\nYou have "<<guesslimit<< " guesses, use them wisely\nIKUZO!!"<<endl;
 	
	  	do{
 		cout<<"Enter a guess for the secret number:";
 	    cin>>input;
 	    if (input< secretnum){
 	    	if ( secretnum - input <= 2 ){
 	    		cout<<"Very warm!\n"<<endl;
			 }
			else {
				cout<<"Warmer...\n"<<endl;
			}
		 }
		 else if (input== secretnum){
		 	cout<< "Congratuations! you have guessed the secret number\n"<<endl;
		 	
			 cout<<"In only "<<guesscount + 1<<" guesses!\n"<<endl;
		 }
		 
		 else{
		 
		 	if (input- secretnum <= 2) {
		 		cout<< "Just a little colder...\n"<<endl;
			 }
			 else{
			 	cout<< "Colder...\n"<<endl;
			 }
		 }
 	    
 	    guesscount++;
 	    
 	    if (input != secretnum ) {
 	    	  cout<<"You have "<< guesslimit - guesscount<<" guesses left\n"<<endl;
		 }
 	  
 	
	 }while(input!= secretnum && guesscount < guesslimit);
	 
	
	//what happens when user runs out of guesses
	 if (guesscount >= guesslimit){
	 	cout<<"\nGAME OVER!\nSorry :( You ran out of guesses"<<endl;
	 }
	
	 
	 
	 //code to reveal secret number
	 cout<<"Do you want to know the secret number? (Y/N):"<<endl;
	 cin>> input2;
	 
	 if (input2 == 'y'|| input2== 'Y'){
	 	cout << "The secret number was "<<secretnum<< " :P\nCome back soon!";
	 }
	 
	 else if (input2 == 'n'|| input2== 'N') {
	 	cout<<"Okay, come back soon!"<<endl;
	 }
	 
 	
 	return 0;	
 }