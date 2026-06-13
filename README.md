# Eye of Providence — Team 5590 (The Alumiboti) Shared Library

Welcome to the **Eye of Providence**, Team 5590's common Java library! This repository hosts our reusable subsystems, custom command wrappers, math utilities, and vision/sensor abstractions that we carry over from season to season. 

Instead of copying and pasting code every January, we maintain it here to ensure it stays tested, optimized, and easy to deploy across different robot years.

---

## 🚀 How to Integrate with Your Robot Code

The easiest way to use the Eye of Providence in a standard WPILib robot project is by using a **Vendor Dependency JSON file**.

1. Create a file named `EyeOfProvidence.json` inside your robot project's `vendordeps/` directory.
2. Paste the configuration template below into that file.
3. Click **Build Robot Code** in VS Code to let Gradle pull the library automatically.

### VendorDep JSON Template
```json
{
  "name": "EyeOfProvidence",
  "version": "1.0.0",
  "uuid": "5590b0ti-1337-4fc7-bc89-eyeofprov111",
  "mavenUrls": [
    "https://alumiboti5590.github.io/eye-of-providence/"
  ],
  "javaDependencies": [
    {
      "groupId": "com.alumiboti5590",
      "artifactId": "eyeofprovidence",
      "version": "1.0.0"
    }
  ],
  "cppDependencies": []
}
```
*(Note: Be sure to change the username in the `mavenUrls` if your GitHub Organization uses a different name).*

---

## 🛠️ Local Development

Want to add a new wrapper or fix a bug? Here is how to work on the library locally.

### Prerequisites
* **Java Development Kit (JDK) 17** (or the current version used by the active WPILib season).
* Visual Studio Code with the WPILib extension, or IntelliJ IDEA.

### Useful Commands
Open your terminal in the root directory of this project and run the following commands:

* **Compile the code:**

  ```bash
  ./gradlew compileJava
  ```
* **Run unit tests:**

  ```bash
  ./gradlew test
  ```
* **Build and verify everything:**

  ```bash
  ./gradlew build
  ```

### 💡 Pro-Tip: Testing Changes Locally
Before publishing a version to the entire team, you can test your changes on a real robot project locally using **Maven Local**.

1. In this library repository, run:
   ```bash
   ./gradlew publishToMavenLocal
   ```
   *This copies the compiled library directly into a hidden folder on your computer (`~/.m2/repository`).*
2. In the Alumiboti main robot project's `build.gradle`, ensure `mavenLocal()` is added to your repositories block:
   ```groovy
   repositories {
       mavenLocal()
       mavenCentral()
   }
   ```
3. Your local robot project will now prioritize your modified local version over the live internet version!

---

## 📦 How to Publish a New Version

We use GitHub Actions to automate our releases. When you push a version tag, GitHub will automatically compile the code and update our GitHub Pages Maven repository.

### Step-by-Step Release Process

1. **Update the Version:** Open `build.gradle` and update the `version` string (e.g., change `version = '1.0.0'` to `version = '1.1.0'`).
2. **Commit and Push:** Commit your changes and push them to the main branch.
```bash
   git add build.gradle
   git commit -m "Bump version to 1.1.0"
   git push origin main
   ```
3. **Tag the Release:** Create a git tag that matches your version number prefixed with a `v`, then push the tag.
```bash
   git tag v1.1.0
   git push origin v1.1.0
   ```

🔔 **What happens next?** The push triggers our `Publish Maven Repository` action. Within 2 minutes, the new version will be live on our GitHub Pages site and available for the robot code to use! Remember to update the version number inside your robot project's `vendordeps/EyeOfProvidence.json` file to fetch the new updates.