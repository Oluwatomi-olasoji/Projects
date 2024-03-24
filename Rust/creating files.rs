use std::io::Write;
use std::io;

//note to self: fix panicking loops with incorrect answers

fn code_7(mut paranum: i32){

let departments= ["Strategy by EY-Parthenon", "Consulting", "People and workforce","Transactions and corporate finance", "Tax","Assurance"];

let consulting= ["Analytics consulting services", "Customer experience","Cybersecurity, strategy, risk, compliance and resilience",
               "Digital transformation", "Risk consulting services","Supply chain and operations","Technology transformation"];

let assurance= ["Audit services","Climate change and sustainability services","Financial accounting advisory services",
               "Forensic and integrity services","Private client audit experience","Accounting Link",
               "Assurance"];



if paranum == 1{
let mut file = std::fs::File::create("aigbona_juliet.txt").expect("create failed");
file.write_all("Aigbona Juliet B.Sc\n".as_bytes()).expect("write failed");
file.write_all(departments[1].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. consulting.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(consulting[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(consulting[consulting.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}

if paranum == 2{
let mut file = std::fs::File::create("akpevwe_iloka.txt").expect("create failed");
file.write_all("Akpevwe Iloka HND\n".as_bytes()).expect("Write failed");
file.write_all(departments[5].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. assurance.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(assurance[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(assurance[assurance.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}
} 


fn code_8(mut paranum: i32){

let departments= ["Strategy by EY-Parthenon", "Consulting", "People and workforce","Transactions and corporate finance", "Tax","Assurance"];

let P_and_W= ["Change management and experience","HR transformation","Integrated workforce mobility","Learning and development consulting",
              "Recognition and reward advisory","Workforce analytics","People and workforce"];

let tax= ["Tax planning","Tax functions and operations","Tax policy and controversy","Global trade",
         "Tax accounting","Tac compliance","Transaction tax"];



if paranum == 1{
let mut file = std::fs::File::create("adamu_sagamu.txt").expect("create failed");
file.write_all("Adamu Sagamu B.Sc\n".as_bytes()).expect("write failed");
file.write_all(departments[4].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. tax.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(tax[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(tax[tax.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}

if paranum == 2{
let mut file = std::fs::File::create("gbenga_daniels.txt").expect("create failed");
file.write_all("Gbenga Daniels HND\n".as_bytes()).expect("Write failed");
file.write_all(departments[2].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. P_and_W.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(P_and_W[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(P_and_W[P_and_W.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}
} 



fn code_9(mut paranum: i32){

let departments= ["Strategy by EY-Parthenon", "Consulting", "People and workforce","Transactions and corporate finance", "Tax","Assurance"];

let strategy= ["Strategy consulting", "Corporate and growth strategy","Transaction strategy and execution",
             "Restructuring and turnaround strategy", "Industry strategy","Industry strategy","Commercial strategy"];

let T_and_CF= ["Corporate finance","Divestments and carve-outs","Sustainability and ESG Services",
              "M&A advisory","M&A integration","M&A technology and tools","M&A advanced analytics"];




if paranum == 1{
let mut file = std::fs::File::create("ehis_ero.txt").expect("create failed");
file.write_all("Ehis Ero HND\n".as_bytes()).expect("Write failed");
file.write_all(departments[0].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. strategy.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(strategy[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(strategy[strategy.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}

if paranum == 2{

let mut file = std::fs::File::create("maria_akinsola.txt").expect("create failed");
file.write_all("Maria Akinsola M.Sc\n".as_bytes()).expect("write failed");
file.write_all(departments[3].as_bytes()).expect("Write failed");
file.write_all("\n\nRoles include:\n".as_bytes()).expect("Write failed");

    for x in 0.. T_and_CF.len()-1{
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(T_and_CF[x].as_bytes()).expect("Write failed");
    file.write_all(";\n".as_bytes()).expect("Write failed");
   }
    file.write_all("-".as_bytes()).expect("Write failed");
    file.write_all(T_and_CF[T_and_CF.len()-1].as_bytes()).expect("Write failed");
    file.write_all(".".as_bytes()).expect("Write failed");

}
} 


fn main() {

    println!("Whose file would you like to see?\n Press 7 for:\n Aigbona Juliet\n Akpevwe Iloka\n\n Press 8 for:\n Adamu Sagamu\n Gbenga Daniels\n\n Press 9 for:\n Ehis Ero\n Maria Akinsola\n\n");
    let mut code1 = String::new();
    loop{
    io::stdin().read_line(&mut code1).expect("Failed to read input");
    let code:i32= code1.trim().parse().expect("Please enter an integer");

    if code==7{
       println!("Press:\n1- Aigbona Juliet\n2- Akpevwe Iloka");
       let mut num1 = String::new();
      
       loop{
       io::stdin().read_line(&mut num1).expect("Failed to read input");
       let num:i32= num1.trim().parse().expect("Please enter an integer");

 
       if num==1{
        code_7(num);
        println!("File created");
        break;

       }
       else if num==2 {
        code_7(num);
        println!("File created");
        break;
       }
       else{
        println!("Invalid input, please enter 1 or 2");
       }
       }

    break;
    }

    else if code==8 {
       println!("Press:\n1- Adamu Sagamu\n2- Gbenga Daniels");
       let mut num1 = String::new();
      
       loop{
       io::stdin().read_line(&mut num1).expect("Failed to read input");
       let num:i32= num1.trim().parse().expect("Please enter an integer");

 
       if num==1{
        code_8(num);
        println!("File created");
        break;

       }
       else if num==2 {
        code_8(num);
        println!("File created");
        break;
       }
       else{
        println!("Invalid input, please enter 1 or 2");
       }

    }
    break;
}
   
   else if code==9{
     println!("Press:\n1- Ehis Ero\n2- Maria Akinsola");
       let mut num1 = String::new();
      
       loop{
       io::stdin().read_line(&mut num1).expect("Failed to read input");
       let num:i32= num1.trim().parse().expect("Please enter an integer");

 
       if num==1{
        code_9(num);
        println!("File created");
        break;

       }
       else if num==2 {
        code_9(num);
        println!("File created");
        break;
       }
       else{
        println!("Invalid input, please enter 1 or 2");
       }

    }
    break;

   }
   else {
    println!("Invalid input, please enter either 7,8 or 9");
   }

  
}
}
 

