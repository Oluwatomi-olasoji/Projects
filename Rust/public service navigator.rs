use std::io;

fn main() { 

println!("Which division service do you require? \n Press 1 for geoplitical zoning \n Press 2 for Public service checker");
let mut service= String::new();
	io::stdin().read_line(&mut service).expect("Not a valid input");
	let service:i32 = service.trim().parse().expect("Please enter either 1 or 2");

if service==1{
	GeoPo_Merger();
}
else if service == 2 {
	Pub_Service();
}

}


fn GeoPo_Merger(){
let commissioner:[&str;5]= ["Aigbogun Alamba Daudu", "Murtala Afeez Bendu", 
	                             "Okorocha Calistus Ognona","Adewale Jimoh Akanbi",
	                             "Osazuwa Faith Etieye"];

let ministry:[&str;5]= ["Internal Affairs", "Justice", "Defense", "Power & Steel", "Petroleum"];
let geopozone:[&str;5]= ["South West", "North East", "South South", "South West", "South East"];

println!("List of convicted Ministers");
for i in 0..commissioner.len(){
println!("\n {}, Ministry of {}, {}",commissioner[i],ministry[i],geopozone[i] );
}

}



fn Pub_Service() {
let years= ["1-2","3-5","5-8","8-10","10-13","14"];
let offices= ["Office Aministrator", "Academic", "Lawyer","Teacher"];

println!("Are you a pubilic servant?\n Enter 1 for yes, or 0 for no: ");
let mut ans= String::new();
io::stdin().read_line(&mut ans).expect("Not a valid input");
let ans:i32 = ans.trim().parse().expect("Please enter either 1 or 0");
//////////////////////////////////
if ans==1{

println!("What office in public serice do you hold? Enter;\n 1 for Office Administrator\n 2 for Academic\n 3 for Lawyer\n 4 for Teacher\n");
let mut puboff= String::new();
io::stdin().read_line(&mut puboff).expect("Not a valid input");
let puboff:i32 = puboff.trim().parse().expect("Please enter either 1,2,3 or 4");

println!("How many years of experience do you have? Enter;\n 1 for 1-2 years\n 2 for 3-5 years\n 3 for 5-8 years\n 4 for 8-10 years\n 5 for 10-13 years\n 6 for 14\n");
let mut yearsexp= String::new();
io::stdin().read_line(&mut yearsexp).expect("Not a valid input");
let yearsexp:i32 = yearsexp.trim().parse().expect("Please enter either 1,2,3,4,5 or 6");

if puboff==1{
   if yearsexp==1{
   	println!("Intern");
   }

   else if yearsexp==2 {
   	println!("Administrator");
   }
   else if yearsexp==3{
   	println!("Senior Administrator");
   }
   else if yearsexp==4 {
   	println!("Office Manager");
   }

  else if yearsexp==5{
   	println!("Director");
   }

   else if yearsexp==6 {
   	println!("CEO");
   }
   else{
   	println!("Invalid input");
   }


}
else if puboff==2 {
   if yearsexp==1{
   	println!("No position available");
   }

   else if yearsexp==2 {
   	println!("Research asssitant");
   }
   else if yearsexp==3 {
   	println!("PhD candidate");
   }
   else if yearsexp==4 {
   	println!("Post-Doc Researcher");
   }

  else if yearsexp==5{
   	println!("Senior lecturer");
   }

   else if yearsexp==6 {
   	println!("Dean");
   }
   else{
   	println!("Invalid input");
   }

else if puboff==3 {
   if yearsexp==1{
   	println!("Paralegal");
   }

   else if yearsexp==2 {
   	println!("Junior Associate");
   }
   else if yearsexp==3 {
   	println!("Associate");
   }
   else if yearsexp==4 {
   	println!("Senior Associate 1-2");
   }

  else if yearsexp==5{
   	println!("Senior Associate 3-4");
   }

   else if yearsexp==6 {
   	println!("Partner");
   }
   else{
   	println!("Invalid input");
   }

else if puboff==4 {
   if yearsexp==1{
   	println!("Placement");
   }

   else if yearsexp==2 {
   	println!("Classrom Teacher");
   }
   else if yearsexp==3 {
   	println!("Senior Teacher");
   }
   else if yearsexp==4 {
   	println!("Leading teacher");
   }

  else if yearsexp==5{
   	println!("Deputy Principal");
   }

   else if yearsexp==6 {
   	println!("Principal");
   }
   else{
   	println!("Invalid input");
   }

else {
	println!("Invalid input");
}


}
else if ans==0 {
	println!("Sorry you do not qualify for this promotion");
}

else {
	println!("Invalid input, Please enter either 1 or 0");
}



}



  
