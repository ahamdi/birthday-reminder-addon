# Birthdays Reminder Addon
## What is it ?
This is an addon for eXo Platform 4. It reminds about the birthdays of the users registered within the platform.

## Features
1. A portlet is added to the Home page showing a list of people having birthdays in the next days
2. A programmatic way to enable events once someone celebrates his birthday

## How to Install
You need to use the eXo Platform Addon Manager to install Birthdays Reminder Addon

## Configuration
This addon could be customized by modifying the exo.properties file. Please see how that is done in [eXo Platform documentation](http://docs.exoplatform.org).

* **exo.addons.birthdaysreminder.service.period** : the number of days in which the service will lookup for user birthdays. Default value : 7
* **exo.addons.birthdaysreminder.service.batch** : is the number of users loaded by the service at a time when initiating the service data. Default value : 10 
* **exo.addons.birthdaysreminder.service.mode** : specifies what kind of users will appear in the portlet.Possible values **all** - **contacts** . Default value : all
* **exo.addons.birthdaysreminder.job.expression** : the Cron expression for the job that collects the birthdays daily and triggers the events. Default value : 15 1 0 * * ? * -> Daily at 00:01:15 

## Roadmap
TODO
