use std::io;
use std::io::Write;
use std::io::Read;

fn main() {
    println!("Welcome to Lagoon Hospital Filing system\n\nPress:\n 1 - if you are a physician\n 2- if you are a paitient ");
    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("invalid input");
    let input1:i32 = input.trim().parse().expect("invalid number");

   if input1== 1{
  

   let username = ["g-audu@patron.org","a-seidu@patron.org","m-gbenga@patron.org"];
   let userspecs = ["Orthopedic","Surgery","Pediatrics"];
   
   println!("Welcome Doctor!");
   loop{
   println!("Please Enter your email:");
   let mut email1 = String::new();
   io::stdin().read_line(&mut email1).expect("invalid input");
   let email:String = email1.trim().parse().expect("invalid input");

   println!("Please Enter your specialization:");
   let mut specs1 = String::new();
   io::stdin().read_line(&mut specs1).expect("invalid input");
   let specs: String= specs1.trim().parse().expect("incorrect");

   
    if (email== username[0] && specs == userspecs[0])|| (email== username[1] && specs == userspecs[1]) || (email== username[2] && specs == userspecs[2]) {
      hospital_db();
      break;  
    }
    else {
        println!("Invalid User");
    }
    
    }//end of loop
    }//end of if physician statement



   else if input1==2{
   let pemails = ["a-simon@gmail.com","f-tina@gami.com","d-valerie@gmail.com","s-samuel@gmail.com","o-feji@gmail.com","m-kabir@gmail.com","a-jane@gmail.com","m-ali@gmail.com","o-chisom@gmail.com","e-agatha@gmail.com"]

   println!("Welcome\n");
   println!("Please Enter your email:");
   let mut paiemail1 = String::new();
   io::stdin().read_line(&mut paiemail1).expect("invalid input");
   let paiemail:String = paiemail1.trim().parse().expect("invalid input");
    
   
   for x in 0..10{
    if paiemail == pemails[x]{
        println!("What allergic reaction do you have?:\n");
        println!("Press:\n1- Low Sugar\n2- Low Cholesterol\n3- Diabetes\n 4- Anaphylaxis\n 5- Fish\n");

        let mut allergy1 = String::new();
        io::stdin().read_line(&mut allergy1).expect("invalid input");
        let allergy:i32 = allergy1.trim().parse().expect("invalid number")
        
        
        if allergy < 3{
         allergy_less_3();
        } 

        else if allergy > 3{
            allergy_greater_3();
        }

     
    } //end of if statement that checks cridentials

    else {
        println!("No Access Permission");
    }
   } //end of for loop to check through cridentials

    }


}



fn hospital_db(){
 println!("Here is the content of the oolasoji_db for Lagoon hospital");
 println!("-----------------------------------------------------------\n");

 let mut file= std::fs::File::open("oolasoji_db.sql").unwrap();
 let mut contents = String::new();
 file.read_to_string(&mut contents).unwrap();
 print!("{}",contents );
   
}




fn allergy_less_3(){
println!("Enter your first name\n");

let mut fname1 = String::new();
io::stdin().read_line(&mut fname1).expect("invalid input");
let fname:String = fname1.trim().parse().expect("invalid input");

if fname== "Tina" || fname== "tina"{
let mut x= 0;
let mut y= 3;
}
else if fname== "Valerie" || fname== "valerie"{
let mut x= 1;
let mut y= 1;
}
else if fname== "Feji" || fname== "feji"{
let mut x= 2;
let mut y= 3;
}
else if fname== "Jane" || fname== "jane"{
let mut x= 3;
let mut y= 3;
}
else if fname== "Agatha" || fname== "agatha"{
let mut x= 4;
let mut y= 2;
}



Let names = ["Fagbemi Tina", "Dalegi Valerie", "Oghenero Feji","Alokwe Jane","Eze Agatha"];
let emailz = ["f-tina@gami.com","d-valerie@gmail.com",,"o-feji@gmail.com",,"a-jane@gmail.com","e-agatha@gmail.com"];
let dob = ["3/12/1989","11/1/1993","10/6/1991","28/11/1988","22/04/1996"];

let mut file1= std::fs::File::create("allergy_less_3.txt").expect("create failed");
file1.write_all("Patient Details\n".as_bytes()).expect("Write failed"); 
file1.write_all(name[x].as_bytes()).expect("Write failed"); 
file1.write_all("\n".as_bytes()).expect("Write failed");
file1.write_all(emailz[x].as_bytes()).expect("Write failed");
file1.write_all("\n".as_bytes()).expect("Write failed");
file1.write_all(dob[x].as_bytes()).expect("Write failed");
file1.write_all("\n\n".as_bytes()).expect("Write failed");
file1.write_all("\n\n".as_bytes()).expect("Write failed");

file1.write_all("Physician Details\n".as_bytes()).expect("Write failed");

if y== 1{ 
file1.write_all("Audu Gloria\n".as_bytes()).expect("Write failed"); 
file1.write_all("g-audu@patron.org\n".as_bytes()).expect("Write failed");
file1.write_all("Orthopedic".as_bytes()).expect("Write failed");
}

else if y== 2{
file1.write_all("Seidu Ahmed\n".as_bytes()).expect("Write failed");
file1.write_all("a-seidu@patron.org\n".as_bytes()).expect("Write failed");
file1.write_all("Surgery".as_bytes()).expect("Write failed");
}

else if y== 2{
file1.write_all("Gbenga Mildred\n".as_bytes()).expect("Write failed");
file1.write_all("m-gbenga@patron.org\n".as_bytes()).expect("Write failed");
file1.write_all("Pediatrics".as_bytes()).expect("Write failed");
}



}



fn allergy_greater_3(){
    println!("Enter your first name\n");

let mut fname2 = String::new();
io::stdin().read_line(&mut fname2).expect("invalid input");
let fname3:String = fname2.trim().parse().expect("invalid input");

if fname3== "Samuel" || fname3== "samuel"{
let mut x= 0;
let mut y= 4;
}
else if fname3== "Kabir" || fname3== "kabir"{
let mut x= 1;
let mut y= 3;
}
else if fname3== "Chisom" || fname3== "chiso,"{
let mut x= 2;
let mut y= 4;
}

    let namez1 = ["Salami Samuel", "Mustapha Kabir", "Okonkwo Chisom"];
    let emailz1 = ["s-samuel@gmail.com", "m-kabir@gmail.com","o-chisom@gmail.com"];
    let dob1 = ["21/7/1998","13/05/1990","15/11/1999"];

    let all1 = ["Low sugar","Low cholesterol","Diabetes","Anaphylaxis","Fish"];
    let symp = ["Feeling shaky or trembling.","Changes in your mood,sleep,or eating patterns.","Increased thirst.","Rapid fall in blood pressure.","Vomitting and diarrhea."];

let mut file2= std::fs::File::create("allergy_less_3.txt").expect("create failed");
file2.write_all("Patient Details\n".as_bytes()).expect("Write failed"); 
file2.write_all(namez1[x].as_bytes()).expect("Write failed"); 
file2.write_all("\n".as_bytes()).expect("Write failed");
file2.write_all(emailz1[x].as_bytes()).expect("Write failed");
file2.write_all("\n".as_bytes()).expect("Write failed");
file2.write_all(dob1[x].as_bytes()).expect("Write failed");
file2.write_all("\n\n".as_bytes()).expect("Write failed");
file2.write_all("\n\n".as_bytes()).expect("Write failed");

file2.write_all("Allergy Details\n".as_bytes()).expect("Write failed");
file2.write_all(all1[y].as_bytes()).expect("Write failed"); 
file2.write_all("\n".as_bytes()).expect("Write failed");
file2.write_all(symp[y].as_bytes()).expect("Write failed");


}