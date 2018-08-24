# ExchangeModule

## Module description/purpose

The main purpose of the Exchange module is to provide other systems possibility to communicate with UVMS. What defines as another system, are such as when a third-part system needs to have access or communication with UVMS. A common scenario is when a mobile terminal which is installed in a vessel, need to report its position to UVMS. It can be many different types of mobile terminals from different brands. Therefore Exchange module has provided one interface for these third-part systems to integrate with. The third-part system must need to implement the interface Exchange has provided for exchanging information with the UVMS.  These middle-wares are called for plugins in the UVMS project. There are different types of plugins and will be described later in this documentation.

The main function is making it possible to send a position of a vessel to the UVMS. What happens when a vessel sends its position; is a position report will send from the mobile terminal to a specific plugin and from the plugin the position report will transmit to Exchange module. There are occasions where a vessel can be out of range and not able to send a position report. Then all the positions will be saved to the hard driver in the mobile terminal and tries to resend it as soon as the mobile service is available. 
The communication can be reversed as well, from the UVMS to the mobile terminal. All communication with the third-part system will go through the Exchange module.  In this project you can send polls to a mobile terminal via Exchange module. A poll is a command you send to the mobile terminal to execute a certain action. There are three types of polls; manual poll, configuration poll and sampling poll. 

* Manual poll is a command sends to the mobile terminal to request its latest position. 
* Configuration poll is a command send to mobile terminal to update the configuration of a mobile terminal. 
* Sampling poll is a command sends to the mobile terminal to request it to send a set of positions which have been saved in the mobile terminal. 

Beside the plugin for communication between a vessel and the UVMS, there are other plugins to support other purposes as well, e.g. an email plugin for sending email to notify a system user when an alarm has been triggered. 

## Module dependencies

|Name |Description                                                    |Repository                                  |
|-----|---------------------------------------------------------------|--------------------------------------------|
|User |Authentication operations and access management                |https://github.com/UnionVMS/UVMS-User       |
|Asset|Module to manage a aircraft, watercraft or vehicle             |https://github.com/UnionVMS/UVMS-AssetModule|
|Rules|Module for validation of position report                       |https://github.com/UnionVMS/UVMS-RulesModule|

## JMS Queue Dependencies
The jndi name example is taken from wildfly8.2 application server

|Name                   |JNDI name example          |Description                             |
|-----------------------|---------------------------|----------------------------------------|
|UVMSExchangeEvent|java:/jms/queue/UVMSExchangeEvent|Request queue to Exchange service module|
|UVMSExchange     |java:/jms/queue/UVMSExchange     |Response queue to Exchange module       |
|UVMSUserEvent    |java:/jms/queue/UVMSUserEvent    |Request queue to User service module    |
|UVMSRulesEvent   |java:/jms/queue/UVMSRulesEvent   |Request queue to Rules service module   |

## Datasources
The jndi name example is taken from wildfly8.2 application server

|Name                   |JNDI name example                   |
|-----------------------|------------------------------------|
|uvms_exchange          |java:jboss/datasources/uvms_exchange|

## Related Repositories

* https://github.com/UnionVMS/UVMS-ExchangeModule-DB
* https://github.com/UnionVMS/UVMS-ExchangeModule-MODEL
* https://github.com/UnionVMS/UVMS-AIS-PLUGIN
 * * https://github.com/UnionVMS/UVMS-AIS-RESOURCE-ADAPTER
* https://github.com/UnionVMS/UVMS-NAF-PLUGIN
* https://github.com/UnionVMS/UVMS-SWAgenscyEmail-PLUGIN
* https://github.com/UnionVMS/UVMS-Email-PLUGIN'
* https://github.com/UnionVMS/UVMS-SiriusOne-PLUGIN
* https://github.com/UnionVMS/UVMS-TwoStage-PLUGIN
* https://github.com/UnionVMS/UVMS-FLUX-PLUGIN
 * https://github.com/UnionVMS/UVMS-ExchangeModule-JAXB-CLIENT
