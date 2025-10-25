use greeter::greet;

fn main() {
    println!("{}", greet(None));
    println!("{}", greet(Some("Alice")));
}
