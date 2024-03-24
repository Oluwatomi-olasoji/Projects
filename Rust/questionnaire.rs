use std::io;

fn main() {
    //opening statemeant and creation of variable for number of siblings
    println!("\n\nHello there, welcome to this questionnaie! Please enter only numerial values where applicable \nHow many siblings do you have?");
    let mut numsib = String::new();
    io::stdin().read_line(&mut numsib).expect("Cannot read input");
    let numsib1:i32 = numsib.trim().parse().expect("Not a valid number");

//for loop to ask the questions as many times as the number of siblings
    for num in 1.. numsib1 + 1 {
        println!("Enter fullname of sibling {}",num);
        let mut name = String::new();
        io::stdin().read_line(&mut name).expect("Cannot read input");
        let name = name.trim(); //necessary to prevent space after {} when print
         
        println!("Enter {}'s age",name);
        let mut age = String::new();
        io::stdin().read_line(&mut age).expect("Cannot read input");
        let age1:i32 = age.trim().parse().expect("Not a valid number");
        
       
        if age1 > 18 {
        
        loop {
            //creation of variable 'status' to hold marital satus
            println!("Is {} married? Y/N",name);
            let mut status = String::new();
            io::stdin().read_line(&mut status).expect("Cannot read input");
            let status = status.trim(); //this is necessary for the if statement to recognize y and n, as the readline adds a space to the input

            //if statement for if sibling is married
            if status=="y"|| status== "Y"{
              loop{
              //creation of offspring variable to retain wether the sibling has any offspring
              println!("Does {} have any childern (Y/N)",name);
              let mut offspring  = String::new();
              io::stdin().read_line(&mut offspring).expect("Cannot read input");
              let offspring= offspring.trim();

                 if offspring == "y"|| offspring== "Y" {
                 break;
                 }

                 else if offspring == "n"|| offspring== "N"{
                 break;
                 }

                else{
                println!("Invalid input, please enter Y/N")
                }
              }

                //creation of city varable to retain which city a sibling w kids lives in
                 println!("What city does {}'s family live in",name);
                 let mut city  = String::new();
                 io::stdin().read_line(&mut city).expect("Cannot read input");

              break; //break statemtent to get out of the 'is sibling married loop'. so yes to that stament leads to a break

            } //and of if married statement
 

            //else if statement for if sibling is not married
            else if status == "n"|| status== "N"{
            let mut proffession  = String::new(); /*creation of profession variable to retain wether sibling is single or a worker 
                                                   profeesion was defined outside the loop so it can be used outside the loop in ln80*/

              loop{ //loop for if sibling is student or a worker
                    
                  println!("Is {} a student or a worker (S/W)",name);
                  io::stdin().read_line(&mut proffession).expect("Cannot read input");
                  let proffession= proffession.trim();

                  if proffession != "s" && proffession!= "S" && proffession!= "w"&& proffession!= "W"{ //if statement to account for errors when answering if student or worker
                  println!("Invalid input, please enter S/W");
                  }
 
                  else {
                    if proffession == "s"|| proffession== "S" {
                    //if sibling is a student statement
                    //creation of variable to hold university of student sibling
                    println!("What university does {} attend",name);
                    let mut uni  = String::new();
                    io::stdin().read_line(&mut uni).expect("Cannot read input");
            
                    println!("What course does {} study",name);
                    let mut course  = String::new();
                    io::stdin().read_line(&mut course).expect("Cannot read input");
                    } 
                    
                    break; //else, as in s or w, is a way to break out of 'is sibling a student or worker loop'
                    }

                } //end of student ot worker loop
               
            
              break; //break statemtent to get out of the 'is sibling married loop'. so no to that stament leads to a break 
            }



            else{ //else statement for if neither y/n was answered to the if sibling is married loop
               println!("Invalid input, please enter Y/N");

              }             //end of else statment for if neither Y/N is input as an ansewer to the "is sibling married loop"
            }              //end of is sibling married loop
        }                 //end of if 18 stament


        

        
       else { //else staement for if age1 is 18 or below

        loop{ //'has sibling written waec' loop
              println!("Has {} written WAEC ? (Y/N)",name);
              let mut waecstatus  = String::new();
              io::stdin().read_line(&mut waecstatus).expect("Cannot read input");
              let waecstatus= waecstatus.trim();  //creation of waecstatus variable to retain wether or not sibling under 18 has written waec
            
              if waecstatus == "y"|| waecstatus== "Y"{
              println!("What secondary school did {} attend",name);
              let mut secs  = String::new(); //creation of sec varaible to hold what secondary school sibling attended
              io::stdin().read_line(&mut secs).expect("Cannot read input");
              break; // break for when waecstaus holds yes
              }

              else if  waecstatus == "n"|| waecstatus== "N" {
              println!("What class is {} in?",name);
              let mut class  = String::new(); //creation of class variable to hold what class sibling who has not written waec is in
              io::stdin().read_line(&mut class).expect("Cannot read input");
              break; //break for when waecstaus holds no

              }

              else {
            println!("Invalid input, please enter (Y/N)");
              } //end of else statement for when waecstatus is neither yes nor no
 
            } //end of waec staus loop
              
            } //end of else else staement for if age1 is 18 or below

            println!("***********************************************************************\n\n");


        } //end of for loop thats iterating through number of siblings to ask these questions as many times as ther are siblings

        println!("Thank you for filling this questionnaie, goodbye! <3");


}







