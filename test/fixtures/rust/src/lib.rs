/// Simple greeting function for workflow testing.
pub fn greet(name: Option<&str>) -> String {
    match name {
        Some(n) if !n.trim().is_empty() => format!("Hello, {}!", n),
        _ => "Hello, World!".to_string(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_basic_greeting() {
        assert_eq!(greet(None), "Hello, World!");
    }

    #[test]
    fn test_personalized_greeting() {
        assert_eq!(greet(Some("Alice")), "Hello, Alice!");
    }

    #[test]
    fn test_empty_name() {
        assert_eq!(greet(Some("")), "Hello, World!");
        assert_eq!(greet(Some("   ")), "Hello, World!");
    }
}
