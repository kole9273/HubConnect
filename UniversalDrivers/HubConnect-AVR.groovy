/*
 *	Copyright 2019 Steve White
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *	use this file except in compliance with the License. You may obtain a copy
 *	of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *	License for the specific language governing permissions and limitations
 *	under the License.
 *
 *
 */
metadata 
{
	definition(name: "HubConnect AVR", namespace: "shackrat", author: "Steve White", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/UniversalDrivers/HubConnect-AVR.groovy")
	{
		capability "Switch"
		capability "Refresh"
		capability "Actuator"
		capability "Telnet"
		capability "Initialize"
		capability "AudioVolume"

		attribute "mediaInputSource", "string"
		attribute "version", "string"

		command "setInputSource", [[name:"Id*", type: "NUMBER", description: "Input ID" ]]
		command "sync"
	}
}


/*
	installed
    
	Doesn't do much other than call initialize().
*/
def installed()
{
	initialize()
}


/*
	updated
    
	Doesn't do much other than call initialize().
*/
def updated()
{
	initialize()
}


/*
	initialize
    
	Doesn't do much other than call refresh().
*/
def initialize()
{
	refresh()
}


/*
	parse
    
	In a virtual world this should never be called.
*/
def parse(String description)
{
	log.trace "Msg: Description is $description"
}


/*
	setVolume
    
	Does what it says.
*/
def setVolume(value)
{
	parent.sendDeviceEvent(device.deviceNetworkId, "setVolume", [value])
}


/*
	editCurrentInputName
    
	Does what it says.
*/
def editCurrentInputName(value)
{
	parent.sendDeviceEvent(device.deviceNetworkId, "editCurrentInputName", [value])
}


/*
	setInputSource
    
	Does what it says.
*/
def setInputSource(value)
{
	parent.sendDeviceEvent(device.deviceNetworkId, "setInputSource", [value])
}


/*
	mute
    
	Mutes the device.
*/
def mute()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "mute")
}


/*
	unmute
    
	Unmutes the device.
*/
def unmute()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "unmute")
}


/*
	on
    
	Turns the device on.
*/
def on()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "on")
}


/*
	off
    
	Turns the device off.
*/
def off()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "off")
}


/*
	volumeUp
    
	volumeUp on the device.
*/
def volumeUp()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "volumeUp")
}


/*
	volumeDown
    
	volumeDown on the device.
*/
def volumeDown()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "volumeDown")
}


/*
	refresh
    
	Refreshes the device by requesting an update from the client hub.
*/
def refresh()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
}


/*
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "switch")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Universal", major: 1, minor: 4, build: 0]}
