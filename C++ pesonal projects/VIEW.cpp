//CODE TO VIEW RESULTS (V is used to view)
#include <iostream>
#include <fstream>
#include <string>
#include <iomanip> //for padding (to use setw function which sets the width of the strings so they align 
using namespace std;

int main(){
	char userinput;
	string line;
	int linecount=0; 
	
	//insert code for main menu
	

	cout<<"Enter option: ";
	cin>>userinput;
	if (userinput == 'V' || userinput == 'v'){
		

	string name[36], matno[36], csc201[36],csc205[36],mth201[36],mth205[36],gst201[36],gpaarr[36];
	string stuname, stumat, csc201score,csc205score, mth201score, mth205score, gst201score,gpa;
	
	ifstream mfile("studentResult.txt");
	if (mfile.is_open()){
		while(!mfile.eof()){
			getline (mfile,stuname,',');
			name[linecount]=stuname;
			
			getline (mfile,stumat,',');
			matno[linecount]=stumat;
			
			getline (mfile,csc201score,',');
			csc201[linecount]=csc201score;
			
			getline (mfile,csc205score,',');
			csc205[linecount]=csc205score;
			
			getline (mfile,mth201score,',');
			mth201[linecount]=mth201score;
			
			getline (mfile,mth205score,',');
			mth205[linecount]=mth205score;
			
			getline (mfile,gst201score,',');
			gst201[linecount]=gst201score;
			
			getline (mfile,gpa,'\n');
			gpaarr[linecount]=gpa;
			linecount++;
			
		}
		mfile.close();
	}
	else 
	{
	cout<<"invlaid input";
	//code to return main menue
	}
	
	for(int j=0; j<linecount; j++){
		cout<<setw(25)<<left<<name[j]<<setw(10)<<left<<matno[j]<<left<<"\t"<< csc201[j]<<"\t"<<csc205[j]<<"\t"<< mth201[j]<<"\t"<<mth201[j]<<"\t"<<gst201[j]<<"\t"<<gpaarr[j]<<"\t"<<endl;
	}
	
	return 0;
}
		
	}
