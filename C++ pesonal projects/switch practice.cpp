#include <iostream>
using namespace std;
//how to use switch statement
string getdayoweek(int daynum) {
	string dayname;
	switch (daynum) {
		case 0:
			dayname= "sunday";
			break;
		case 1:
			dayname= "monday";
			break;
		case 2:
			dayname= "tueday";
			break;
		case 3:
			dayname= "wednesday";
			break;
		case 4:
			dayname= "thursday";
			break;
		case 5:
			dayname= "friday";
			break;
		case 6:
			dayname= "saturday";
			break;
		default:
			dayname = "Invalid day number";
			break;
	}

	return dayname;
}

int main() {
	int num;
	while (num != 8){

	cout<<"Enter a number from 0-6, enter 8 to quit:";
	cin>>num;
	
	string result= getdayoweek(num);
	cout<<result<<endl;

}

	return 0;

}