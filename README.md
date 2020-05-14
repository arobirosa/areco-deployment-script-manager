# areco-deployment-script-manager
Simple but powerful Patch system for SAP Commerce Cloud, formely known as SAP hybris

With this extension you can create deployment scripts (patches) which are going to be automatically run during the update running system of the SAP Commerce platform.
It allows you to fully automate the changes in the data that every tickets requires, the early testing of this changes and the reduction of errors related to these changes.

**Disclaimer: this project is not affiliated, associated, authorized, endorsed by, or in any way officially connected with SAP, or any of its subsidiaries or its affiliates.**

## Features
  * Easy creation of the deployment scripts following the principle **convention over configuration**
  * Runs Impex, SQL, **Groovy** and **BeanShell** scripts only once or multiple times
  * Compatible with SAP Commerce 1xxx, 6.x, 5.x and 4.x
  * Runs deployment scripts during the update running system
  * Scripts may be configured to only run on some servers and tenants
  * **Strict error handling** strategy
  * **Commercial Friendly License:** The extension can be used in commercial projects mixed with proprietary code
  * Open Source: You can review, improve or fix the code
  * Free: Please read the motivation section on the wiki

## Advantages over SAP Commerce's patch extension
[What are the advantages of the Areco Deployment Scripts manager compared to the Hybris Patch extension?](https://github.com/arobirosa/areco-deployment-script-manager/wiki/FAQ#what-are-the-advantages-of-the-areco-deployment-scripts-manager-compared-to-the-hybris-patch-extension)

## Installation
[Installation instructions](https://github.com/arobirosa/areco-deployment-script-manager/wiki/Installation)

## Usage
[How to write patches](https://github.com/arobirosa/areco-deployment-script-manager/wiki/How-to-write-deployment-scripts)

[How to run patches](https://github.com/arobirosa/areco-deployment-script-manager/wiki/How-to-run-deployment-scripts)

## Screenshots
This extension addes a new menu in the Backoffice (or hMC)

![New menu in Backoffice](https://github.com/arobirosa/areco-deployment-script-manager/wiki/images/screenshot_backoffice_group.png)

It shows the list of applied patches

![List of applied patches in Backoffice](https://github.com/arobirosa/areco-deployment-script-manager/wiki/images/screenshot_backoffice_executions.png)

It stores the stacktrace of any patch which failed during the execution

![Patch with a failed execution in Backoffice](https://github.com/arobirosa/areco-deployment-script-manager/wiki/images/screenshot_backoffice_executions_with_error.png)

The execution of the patches is logged on the console and you can use ant to apply the patches **without updating the system**

![The execution of the patches is logged on the console](https://github.com/arobirosa/areco-deployment-script-manager/wiki/images/output_console_update_running_system.png)

## Contributing
[How to contribute as an user, translator or developer](https://github.com/arobirosa/areco-deployment-script-manager/wiki/How-to-contribute)
