//Making a power function
#include <iostream>
using namespace std;

int power(int baseNum, int powerNum){
	int result = 1;
	for (int i = 0; i < powerNum; i++){
		result = result * baseNum;
	}
	retrun result;
}
 int main(){
 	int base, pow;
 	char ans;
 	cout<<"Welcome to my simple power caculator, that calulates A^x\n"<<endl;
  do{
  	cout<<"Enter the base number A: ";
 	cin>> base;
 	cout<<endl;
 	cout<< "Enter the power number x: ";
 	cin>>pow;
 	
 	int r = power(base,pow);
 	cout<< base <<" rasied to the power of "<<pow<< " is: "<<r <<" !";
 	cout<<"Do you wish to calculate something else? (Y/N)";
 	cin>>ans;
  }
	
 }