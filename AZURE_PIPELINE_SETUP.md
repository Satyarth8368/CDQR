# Azure Pipeline Configuration Documentation for CDQR

## 📋 Overview
This pipeline automates the complete CI/CD workflow for the CDQR Selenium test automation project.

## 🏗️ Pipeline Architecture

### **4-Stage Pipeline**

```
┌──────────────────────────────────────────────────────┐
│ Build Stage                                          │
│ ├─ Maven Clean                                      │
│ ├─ Maven Compile                                    │
│ └─ Maven Package                                    │
└─────────────────┬──────────────────────────────────┘
                  ↓
┌──────────────────────────────────────────────────────┐
│ Test Stage                                           │
│ ├─ Run QRCD Selenium Tests                         │
│ ├─ Publish Test Results (JUnit)                    │
│ └─ Generate Test Reports & Coverage                │
└─────────────────┬──────────────────────────────────┘
                  ↓
┌──────────────────────────────────────────────────────┐
│ Code Quality Stage                                   │
│ ├─ SonarQube Analysis                               │
│ └─ Publish Quality Metrics                          │
└─────────────────┬──────────────────────────────────┘
                  ↓
┌──────────────────────────────────────────────────────┐
│ Package Stage                                        │
│ ├─ Final Maven Build                                │
│ ├─ Archive Artifacts                                │
│ └─ Create Deployment Package                        │
└──────────────────────────────────────────────────────┘
```

## 🚀 Quick Setup

### Step 1: Add to Repository
The `azure-pipelines.yml` file has been added to the root of your repository.

### Step 2: Create Pipeline in Azure DevOps
1. Go to [Azure DevOps](https://dev.azure.com)
2. Create/Select your project
3. Navigate to **Pipelines > New Pipeline**
4. Select **GitHub** as the source
5. Select **Satyarth8368/CDQR** repository
6. Choose **Existing Azure Pipelines YAML file**
7. Select **azure-pipelines.yml** from main branch
8. Click **Save and run**

### Step 3: Authorize GitHub
- Azure DevOps will request permission to access your GitHub repo
- Approve the authorization

## 📝 Pipeline Configuration Details

### Triggers
```yaml
trigger:
  branches:
    include:
    - main
    - QRCD
    - develop
```
- Pipeline runs automatically on push to these branches
- Excludes updates to README.md, docs, and log files

### Build Pool
```yaml
pool:
  vmImage: 'windows-latest'
```
- Uses Windows environment (required for Edge WebDriver)
- Includes Java, Maven, and build tools pre-installed

### Variables
| Variable | Value | Purpose |
|----------|-------|---------|
| `buildConfiguration` | Release | Maven build profile |
| `MAVEN_CACHE_FOLDER` | `.m2/repository` | Cache Maven dependencies |
| `mavenPomFile` | `**/pom.xml` | Maven configuration file |

## 📊 Stage Details

### Stage 1: Build
Compiles the Java project using Maven
```bash
mvn clean compile package -DskipTests
```
**Output**: Compiled classes and packaged JAR files

### Stage 2: Test
Executes Selenium tests with comprehensive reporting
```bash
mvn test -Dtest=QRCD
```
**Key Features**:
- Runs only QRCD test class
- Single fork (sequential execution for UI tests)
- Publishes JUnit test results
- Generates code coverage reports (JaCoCo)
- Archives test reports as artifacts

### Stage 3: Code Quality
Analyzes code quality metrics (optional)
```bash
mvn verify sonar:sonar -Dsonar.projectKey=CDQR
```
**Features**:
- Conditional execution (set `runSonarQube` variable)
- Reports to SonarQube server
- Includes code coverage metrics

### Stage 4: Package
Creates deployment-ready artifacts
**Output**:
- Compiled JARs/WARs
- ZIP deployment package
- Test reports and artifacts

## 🔑 Key Features

### ✅ Automated Test Execution
- Runs QRCD Selenium tests automatically
- Supports parallel test execution (configurable)
- Captures detailed test results

### ✅ Test Reporting
- JUnit XML test results
- HTML test reports
- Code coverage metrics (JaCoCo)
- Test result trends

### ✅ Artifact Management
- Maven build artifacts cached
- Test reports archived
- Deployment packages created

### ✅ Code Quality Integration
- SonarQube analysis (optional)
- Code coverage tracking
- Quality gates support

### ✅ Error Handling
- Graceful failure handling
- Detailed error logs
- Test result capture on failure

## 📦 Artifacts Generated

| Artifact | Location | Purpose |
|----------|----------|---------|
| `maven-build` | Build artifacts folder | Compiled JARs and dependencies |
| `test-reports` | Surefire reports | JUnit test results and logs |
| `drop` | Final artifacts | Deployment-ready package |
| `deployment` | CDQR-{BuildId}.zip | Complete deployment archive |

## 🔧 Customization

### Running Tests on Specific Branch
Modify the `trigger` section:
```yaml
trigger:
  branches:
    include:
    - production  # Add your branch here
```

### Changing Test Suite
Modify the Maven test command in Stage 2:
```yaml
options: '-Dtest=QRCD#testMethodName'  # Run specific test method
```

### Parallel Test Execution
Change `forkCount` in Test stage:
```yaml
options: '-Dtest=QRCD -DforkCount=4'  # Run 4 parallel threads
```

### Enable SonarQube
Set variable in pipeline:
1. Pipeline > Edit > Variables
2. Add `runSonarQube` = `true`

## 🛡️ Security Considerations

### Store Secrets Safely
Use Azure Key Vault for sensitive data:
```yaml
- task: AzureKeyVault@1
  inputs:
    azureSubscription: 'Your-Subscription'
    KeyVaultName: 'your-keyvault'
    SecretsFilter: '*'
```

### Service Connections
- Create GitHub service connection for repository access
- Create SonarQube service connection for code quality
- Use managed identities when possible

### Branch Protection
1. Go to GitHub Settings > Branches
2. Add branch protection rule for `main`
3. Require successful Azure Pipeline status checks

## 📊 Monitoring & Notifications

### View Pipeline Status
1. Go to Azure DevOps > Pipelines
2. Select "CDQR" pipeline
3. View run history and logs

### Email Notifications
1. Pipelines > Settings > Notifications
2. Add subscribers
3. Select notification events

### Create Dashboard Widget
1. Azure DevOps Dashboard
2. Add "Build Status" widget
3. Configure to show CDQR pipeline

## ❌ Troubleshooting

### Issue: "Tests fail to run"
**Solution**:
- Check pom.xml has correct test configuration
- Verify JUnit dependencies in pom.xml
- Check test class name matches pattern

### Issue: "WebDriver not found"
**Solution**:
- Pipeline uses Windows agent with Edge pre-installed
- Check Edge browser version compatibility
- Update WebDriver to match browser version

### Issue: "SonarQube connection error"
**Solution**:
- Verify SonarQube service connection exists
- Check SonarQube server is accessible
- Verify authentication token is valid

### Issue: "Timeout during test execution"
**Solution**:
- Increase timeout in QRCD.java: `Duration.ofSeconds(15)` → `Duration.ofSeconds(30)`
- Reduce test parallelism: `forkCount=1`
- Add more resources to build agent

## 📈 Performance Tips

### Reduce Build Time
1. **Cache Maven dependencies**: Already configured
2. **Skip unnecessary steps**: Use `condition` statements
3. **Parallel execution**: Increase `forkCount` for tests

### Optimize for CI/CD
1. Set browser to headless mode for UI tests
2. Use timeout thresholds appropriately
3. Cache dependencies between runs

## 🔄 Integration with GitHub

### Status Checks
Pipeline status appears on pull requests:
- ✅ All checks pass → Ready to merge
- ❌ Pipeline fails → Review logs and fix

### Branch Protection Rules
1. Settings > Branches > Branch protection rules
2. Require status checks to pass before merging
3. Select Azure Pipeline check

## 📚 Additional Resources

- [Azure Pipelines Documentation](https://docs.microsoft.com/azure/devops/pipelines/)
- [Maven Integration Guide](https://docs.microsoft.com/azure/devops/pipelines/build/build-maven)
- [Selenium Best Practices](https://www.selenium.dev/documentation/)
- [SonarQube Integration](https://docs.sonarqube.org/latest/analysis/github-integration/)
- [Pom.xml Reference](https://maven.apache.org/pom.html)

## 🎯 Next Steps

1. ✅ Push `azure-pipelines.yml` to repository
2. ✅ Create pipeline in Azure DevOps
3. ⬜ Set up SonarQube service connection (optional)
4. ⬜ Configure email notifications
5. ⬜ Create Azure DevOps dashboard
6. ⬜ Set up branch protection rules

---
**Last Updated**: 2026-05-18
