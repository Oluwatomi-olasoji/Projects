#include <iostream>
#include<fstream>
#include <string>
#include <iomanip>
#define population 100
using namespace std;

//line count in the search function

/*  L O G I N  V A L I D A T O R  F U N C T I O N  */
bool check_auth(string username, string password){
    ifstream infile("users.txt");
    string uname, pass;
    while(infile >> uname >> pass){
        if(uname == username && pass == password){
            return true;
        }
    }
    return false;
}

/*  S E A R C H  F U N C T I O N  */
void searchHandler(){
	int i =0;
	bool stopLoop = false;
	
	string matric;
	string name, mat_no, csc201, csc205, mth201, mth205, gst201, gpa;
	//arrays to store the data from files
	string name_list[population], matric_list[population], csc201_list[population], csc205_list[population], mth201_list[population]; 
	string mth205_list[population], gst201_list[population], gpa_list[population];
	
	//open file in read only mode
	ifstream resultFile;
	resultFile.open("studentResult.txt");
	
	//loop through file until it reaches the end

	while(!resultFile.eof()){
		//getline(input stream, variable to store, when to stop)
		getline(resultFile, name, ',');
		name_list[i] = name;
		getline(resultFile, mat_no, ',');
		matric_list[i] = mat_no;
		getline(resultFile, csc201, ',');
		csc201_list[i] = csc201;
		getline(resultFile, csc205, ',');
		csc205_list[i] = csc205;
		getline(resultFile, mth201, ',');
		mth201_list[i] = mth201;
		getline(resultFile, mth205, ',');
		mth205_list[i] = mth205;
		getline(resultFile, gst201, ',');
		gst201_list[i] = gst201;
		getline(resultFile, gpa, '\n');
		gpa_list[i] = gpa;
		i++;
	}
	cout<<"<---S E A R C H  R E C O R D S--->"<<endl;
	
	// get matric number from user
	cout << "Enter your matric number: ";
	cin >> matric;
	for (int i = 0; i<population; i++) {
		if (matric == matric_list[i]) {
			cout << "Student's Name     " << name_list[i]  << endl;
			cout << "Matric No          " << matric_list[i]<< endl;
			cout << "CSC 201  " << csc201_list[i]<< endl;
			cout << "CSC 205  " << csc205_list[i]<< endl;
			cout << "MTH 201  " << mth201_list[i] << endl;
			cout << "MTH 205  " << mth205_list[i]<< endl;
			cout << "GST 201  " << gst201_list[i]<< endl;
			cout<<endl;
			cout << "GPA      " << gpa_list[i] << "\n" << endl;
			stopLoop = true;
		}
		else {
			continue;
		}	
	}
	//if matric_no wasn't found in array
	if (stopLoop == false) {
			cout <<"No such Matriculation number exists." << endl;
	}
}

/*  V I E W  A L L  F U N C T I O N  */
void viewAllHandler(){
	int lineCount=0;
	
	string name, mat_no, csc201, csc205, mth201, mth205, gst201, gpa;
	//arrays to store the data from files
	string name_list[population], matric_list[population], csc201_list[population], csc205_list[population], mth201_list[population]; 
	string mth205_list[population], gst201_list[population], gpa_list[population];

	ifstream resultFile;
	resultFile.open("studentResult.txt");
	
	if(resultFile.is_open()){
		while(!resultFile.eof()){
		//getline(input stream, variable to store, when to stop)
			getline(resultFile, name, ',');
			name_list[lineCount] = name;
			
			getline(resultFile, mat_no, ',');
			matric_list[lineCount] = mat_no;
			
			getline(resultFile, csc201, ',');
			csc201_list[lineCount] = csc201;
			
			getline(resultFile, csc205, ',');
			csc205_list[lineCount] = csc205;
			
			getline(resultFile, mth201, ',');
			mth201_list[lineCount] = mth201;
			
			getline(resultFile, mth205, ',');
			mth205_list[lineCount] = mth205;
			
			getline(resultFile, gst201, ',');
			gst201_list[lineCount] = gst201;
			
			getline(resultFile, gpa, '\n');
			gpa_list[lineCount] = gpa;
			
			lineCount++;
		}
		resultFile.close();
	}else {
		//code to return main menue
		cout<<"invlaid input";
	}
	
	//output the data to the user
	for(int j=0; j<lineCount; j++){
		cout<<setw(25)<<left<<name_list[j]<<setw(10)<<left<<matric_list[j]<<left<<"\t"<< csc201_list[j]<<"\t"<<csc205_list[j]<<"\t"<< mth201_list[j]<<"\t"<<mth201_list[j]<<"\t"<<gst201_list[j]<<"\t"<<gpa_list[j]<<"\t"<<endl;
	}
	cout<<endl;
}

/*  U P D A T E  F U N C T I O N  */
void updateHandler(){
	int lineCount=0;
	bool stopLoop = false;
	
	string name, mat_no, csc201, csc205, mth201, mth205, gst201, gpa,studentMatNo;
	
	// variables to store the updated input
	string ncsc201, ncsc205, nmth201, nmth205, ngst201, ngpa;
	//arrays to store the data from files
	string name_list[population], matric_list[population], csc201_list[population], csc205_list[population], mth201_list[population]; 
	string mth205_list[population], gst201_list[population], gpa_list[population];

	fstream resultFile("studentResult.txt");
	
	if(resultFile.is_open()){
		while(!resultFile.eof()){
		//getline(input stream, variable to store, when to stop)
			getline(resultFile, name, ',');
			name_list[lineCount] = name;
			
			getline(resultFile, mat_no, ',');
			matric_list[lineCount] = mat_no;
			
			getline(resultFile, csc201, ',');
			csc201_list[lineCount] = csc201;
			
			getline(resultFile, csc205, ',');
			csc205_list[lineCount] = csc205;
			
			getline(resultFile, mth201, ',');
			mth201_list[lineCount] = mth201;
			
			getline(resultFile, mth205, ',');
			mth205_list[lineCount] = mth205;
			
			getline(resultFile, gst201, ',');
			gst201_list[lineCount] = gst201;
			
			getline(resultFile, gpa, '\n');
			gpa_list[lineCount] = gpa;
			
			lineCount++;
			
		}
		resultFile.close();
	}
	else{
		cout<<"An error occured"<<endl;
	}	
	
	cout<<"<---U P D A T E  R E C O R D--->"<<endl;
	cout<<"Enter Student's Matriculation Number:";
	cin>> studentMatNo;
	for (int i = 0; i<lineCount; i++) {
		
		//update code block
		if (studentMatNo == matric_list[i]) {
			cout << "Student's Name     " << name_list[i]  << endl;
			cout << "Matric No          " << matric_list[i]<< endl;
			
			cout << "CSC 201  " << csc201_list[i]<< endl;
			cout<<"Update CSC201 score"<<endl;
			cout<<"Enter score: ";
			cin>>ncsc201;
			
			cout << "CSC 205  " << csc205_list[i]<< endl;
			cout<<"Update CSC205 score"<<endl;
			cout<<"Enter score: ";
			cin>>ncsc205;
			
			cout << "MTH 201  " << mth201_list[i] << endl;
			cout<<"Update MTH201 score"<<endl;
			cout<<"Enter score: ";
			cin>>nmth201;
			
			cout << "MTH 205  " << mth205_list[i]<< endl;
			cout<<"Update MTH205 score"<<endl;
			cout<<"Enter score: ";
			cin>>nmth205;
			
			cout << "GST 201  " << gst201_list[i]<< endl;
			cout<<"Update GST201 score"<<endl;
			cout<<"Enter score: ";
			cin>>ngst201;

			cout << "Previous GPA      " << gpa_list[i] << endl;
			cout<<"Current GPA         ";
			cin>>ngpa;
			cout<<endl;
			
			//update the array with new values	
			csc201_list[i] = ncsc201;
			csc205_list[i] = ncsc205;
			mth201_list[i] = nmth201;
			mth205_list[i] = nmth205;
			gst201_list[i] = ngst201;
			gpa_list[i] = ngpa;
			
			
			cout<<lineCount<<endl<<endl;
			stopLoop = true;		
		}
		else {
			continue;
		}	
	}
	//if matric_no wasn't found in array
	if (stopLoop == false) {
			cout <<"No such Matriculation number exists." << endl;
	}
	
	ofstream outFile("studentResult.txt",ios::out);
	
	if(outFile.is_open()){
		//this is the save the updated result into a new file
		for (int j=0; j<lineCount; j++){
			outFile<<name_list[j]<<","<<matric_list[j]<<","<<csc201_list[j]<<","<<csc205_list[j]<<","<<mth201_list[j]<<","<<mth205_list[j]<<","<<gst201_list[j]<<","<<gpa_list[j]<<"\n";
		}
			
		outFile.close();
		cout<<"Record updated and written to file"<<endl;
	}
	else{
		cout<<"Unable to open file for writing"<<endl;
	}
	
}

/*  S A V E  F U N C T I O N  */
void saveHandler(){
	int lineCount=0;
	bool stopLoop = false;
	string filename;
	
	string name, mat_no, csc201, csc205, mth201, mth205, gst201, gpa,studentMatNo;
	
	// variables to store the updated input
	string nname, nmat_no, ncsc201, ncsc205, nmth201, nmth205, ngst201, ngpa;
	
	//arrays to store the data from files
	string name_list[population], matric_list[population], csc201_list[population], csc205_list[population], mth201_list[population]; 
	string mth205_list[population], gst201_list[population], gpa_list[population];
	
	//open file
	ifstream resultFile;
	resultFile.open("studentResult.txt");
		
	//read contents of file
	if(resultFile.is_open()){
		while(!resultFile.eof()){
		//getline(input stream, variable to store, when to stop)
			getline(resultFile, name, ',');
			name_list[lineCount] = name;
			
			getline(resultFile, mat_no, ',');
			matric_list[lineCount] = mat_no;
			
			getline(resultFile, csc201, ',');
			csc201_list[lineCount] = csc201;
			
			getline(resultFile, csc205, ',');
			csc205_list[lineCount] = csc205;
			
			getline(resultFile, mth201, ',');
			mth201_list[lineCount] = mth201;
			
			getline(resultFile, mth205, ',');
			mth205_list[lineCount] = mth205;
			
			getline(resultFile, gst201, ',');
			gst201_list[lineCount] = gst201;
			
			getline(resultFile, gpa, '\n');
			gpa_list[lineCount] = gpa;
			
			lineCount++;
			
		}
		resultFile.close();
	}
	else{
		cout<<"An error occured"<<endl;
	}
	
	cout<<"S A V E  R E S U L T  T O  F I L E"<<endl;
	cout<<"what will like to name your file, please exclude the type of file to be saved: ";
	cin >> filename;
	cout<<endl;
	cout<<"Enter Student's Matriculation Number: ";
	cin>>studentMatNo;
		
	//search for matriculation no in the array
		for (int i = 0; i<population; i++) {
		if (studentMatNo == matric_list[i]) {
			cout << "Student's Name     " << name_list[i]  << endl;
			cout << "Matric No          " << matric_list[i]<< endl;
			cout << "CSC 201  " << csc201_list[i]<< endl;
			cout << "CSC 205  " << csc205_list[i]<< endl;
			cout << "MTH 201  " << mth201_list[i] << endl;
			cout << "MTH 205  " << mth205_list[i]<< endl;
			cout << "GST 201  " << gst201_list[i]<< endl;
			cout<<endl;
			cout << "GPA      " << gpa_list[i] << "\n" << endl;
			
			
			//update the array with new values
			nname = name_list[i];
			nmat_no = studentMatNo;	
			ncsc201 = csc201_list[i];
			ncsc205 = csc205_list[i]; 
			nmth201 = mth201_list[i];
			nmth205 = mth205_list[i];
			ngst201 = gst201_list[i];
			ngpa = gpa_list[i];
			
			stopLoop = true;
		}
		else {
			continue;
		}	
	}
	//if matric_no wasn't found in array
	if (stopLoop == false) {
			cout <<"No such Matriculation number exists." << endl;
	}
	
	string sourceFile=".txt";
	
	//join the strings together
	string finalname = filename.append(sourceFile);
			fstream outFile;
			outFile.open(finalname,ios::out);
			outFile<<name_list[0]<<","<<matric_list[0]<<","<<csc201_list[0]<<","<<csc205_list[0]<<","<<mth201_list[0]<<","<<mth205_list[0]<<","<<gst201_list[0]<<","<<gpa_list[0]<<"\n";
			outFile<<nname<<","<< ncsc201 <<","<< ncsc205 <<","<<  nmth201 <<","<< nmth205 <<","<< ngst201<<","<<ngpa;
		
			outFile.close();	
}
/*  M E N U  O P T I O N  A R R A Y  */
string mainMenu[]={
	"To SEARCH for a Student's Result --- Press F",
	"TO UPDATE a Student's Result --- Press U",
	"To SAVE a Student's Result to File --- Press P",
	"TO VIEW ALL Results --- Press A",
	"To EXIT the Program --- Press E",
};

int main(){
	bool stopLoop= false;
	bool stopProgram = false;
	string userName,password;
	string menuOption;
	
	//get length of arrays
	int lenOfMainMenu = *(&mainMenu+1)-mainMenu;
	
	cout<<"       				Pan-Atlantic University"<<endl;
	cout<<"       				Result Management System"<<endl<<endl;
	
	cout<<"Provide Your Login Details"<<endl;
	while(!stopLoop){
		cout<<"Enter Username: ";
		cin>>userName;
		cout<<"Enter Password: ";
		cin>>password;
		cout<<endl;
		if(check_auth(userName, password)){
        	cout << "Welcome " << userName << endl<<endl;;
        	stopLoop = true;
    	}else{
        	cout << "Invalid Username and Password" << endl;
        	cout<<"Kindly re-enter a valid Username and password"<<endl;
        	cout<<endl;
    	}
	}
	
	/*  M A I N  M E N U */
	while(!stopProgram){
		cout<<"M A I N  M E N U"<<endl<<endl;
		
		//loop through the main menu array
		for (int i=0; i<lenOfMainMenu;i++){
			cout<<mainMenu[i]<<endl;
		}
		
		cout<<endl;
		cout<<"Enter an option: ";
		cin>>menuOption;
		cout<<endl;
		
		if(menuOption=="E"||menuOption=="e"){
			//exit program
			stopProgram = true;
		}
		else if(menuOption == "F" || menuOption == "f"){
			searchHandler();
		}
		else if(menuOption=="A" || menuOption == "a"){
			viewAllHandler();
		}
		else if(menuOption == "U" || menuOption =="u"){
			updateHandler();
		}
		
		else if(menuOption == "P" || menuOption =="p"){
			saveHandler();
		}
		
		
	}
	
	}
	
