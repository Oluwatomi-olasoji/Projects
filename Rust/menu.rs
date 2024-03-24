use std::io;

fn main() {
    let mut p= String::new();
    let mut f= String::new();
    let mut a= String::new();
    let mut e= String::new();
    let mut w= String::new();

    println!("\n               Menu                        Price\n
              P= Ponded Yam/Edikaiko soup  -N3,200\n
              F= Fried Rice & Chiken       -N3,000\n
              A= Amala & Ewedu Soup        -N2,500\n
              E= Eba & Egusi Soup          -N2,000\n
              W= White Rice & Stew         -N2,500\n");

    println!("Please enter the amount you'd like for the following items: ");
    println!("Ponded Yam/Edikaiko soup:");
    io::stdin().read_line(&mut p).expect("Not a valid input");
    let p1:f32= p.trim().parse().expect("Not a valid number");

    println!("Fried Rice & Chiken:");
    io::stdin().read_line(&mut f).expect("Not a valid input");
    let f1:f32= f.trim().parse().expect("Not a valid number");

    println!("Amala & Ewedu Soup:");
    io::stdin().read_line(&mut a).expect("Not a valid input");
    let a1:f32= a.trim().parse().expect("Not a valid number");

    println!("Eba & Egusi Soup:");
    io::stdin().read_line(&mut e).expect("Not a valid input");
    let e1:f32= e.trim().parse().expect("Not a valid number");

    println!("White Rice & Stew:");
    io::stdin().read_line(&mut w).expect("Not a valid input");
    let w1:f32= w.trim().parse().expect("Not a valid number");

    let p2:f32 = p1 * 3200.0;
    let f2:f32 = f1 * 3000.0;
    let a2:f32= a1 * 2500.0;
    let e2:f32= e1 * 2000.0;
    let w2:f32= w1 * 25000.0;

    let t:f32= p2 + f2 + a2 + e2 + w2;

    if t>= 10000.0 {
        let t2:f32= 0.95 * t;
        println!("Your order costs N{}, inclusive of a 5% discount!", t2);
    }
    else {
        println!("Your order costs N{}",t);
    }

}
