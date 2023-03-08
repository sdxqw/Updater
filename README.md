# Updater
Simple Updater

# Usage
```java
new Updater("https://raw.githubusercontent.com/sdxqw/sdxqw/main/version.txt", "1.0").check((newVersion, currentVersion) -> {
  if (newVersion.equals(currentVersion))
    System.out.println("You are using the latest version.");
  else
    System.out.printf("New version available: %s (current: %s)%n", newVersion, currentVersion);
});
```
