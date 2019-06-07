/**
 * HubConnect Remote Client for Hubitat
 *
 * Copyright 2019 Steve White, Retail Media Concepts LLC.
 *
 * HubConnect for Hubitat is a software package created and licensed by Retail Media Concepts LLC.
 * HubConnect, along with associated elements, including but not limited to online and/or electronic documentation are
 * protected by international laws and treaties governing intellectual property rights.
 *
 * This software has been licensed to you. All rights are reserved. You may use and/or modify the software.
 * You may not sublicense or distribute this software or any modifications to third parties in any way.
 *
 * By downloading, installing, and/or executing this software you hereby agree to the terms and conditions set forth in the HubConnect license agreement.
 * <http://irisusers.com/hubitat/hubconnect/HubConnect_License_Agreement.html>
 * 
 * Hubitat is the trademark and intellectual property of Hubitat, Inc. Retail Media Concepts LLC has no formal or informal affiliations or relationships with Hubitat.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License Agreement
 * for the specific language governing permissions and limitations under the License.
 *
 */
import groovy.transform.Field
import groovy.json.JsonOutput
definition(
	name: "HubConnect Remote Client",
	namespace: "shackrat",
	author: "Steve White",
	description: "Synchronizes devices and events across hubs..",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


// Preference pages
preferences
{
	page(name: "mainPage")
	page(name: "aboutPage")
	page(name: "connectPage")
	page(name: "devicePage")
	page(name: "sensorsPage")
	page(name: "shackratsDriverPage")
	page(name: "switchDimmerBulbPage")
	page(name: "safetySecurityPage")
	page(name: "otherDevicePage")
	page(name: "customDevicePage")
}


// Map containing driver and attribute definitions for each device class
@Field NATIVE_DEVICES =
[
	"arlocamera":		[driver: "Arlo Camera", selector: "arloProCameras", attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arloqcamera":		[driver: "Arlo Camera", selector: "arloQCameras", attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arrival":			[driver: "Arrival Sensor", selector: "smartThingsArrival", attr: ["presence", "battery", "tone"]],
	"button":			[driver: "Button", selector: "genericButtons", attr: ["numberOfButtons", "pushed", "held", "doubleTapped", "button", "temperature", "battery"]],
	"contact":			[driver: "Contact Sensor", selector: "genericContacts", attr: ["contact", "temperature", "battery"]],
	"dimmer":			[driver: "Dimmer", selector: "genericDimmers", attr: ["switch", "level"]],
	"domemotion":		[driver: "Dome Motion Sensor", selector: "domeMotions", attr: ["motion", "temperature", "illuminance", "battery"]],
	"energyplug":		[driver: "DomeAeon Plug", selector: "energyPlugs", attr: ["switch", "power", "voltage", "current", "energy", "acceleration"]],
	"fancontrol":		[driver: "Fan Controller", selector: "fanControl", attr: ["speed"]],
	"garagedoor":		[driver: "Garage Door", selector: "garageDoors", attr: ["door", "contact"]],
	"irissmartplug":	[driver: "Iris Smart Plug", selector: "smartPlugs", attr: ["switch", "power", "voltage", "ACFrequency"]],
	"irisv3motion":		[driver: "IrisV3 Motion Sensor", selector: "irisV3Motions", attr: ["motion", "temperature", "humidity", "battery"]],
	"keypad":			[driver: "Keypad", selector: "genericKeypads", attr: ["motion", "temperature", "battery", "tamper", "alarm"]],
	"lock":				[driver: "Lock", selector: "genericLocks", attr: ["lock", "lockCodes", "codeChanged", "codeLength", "maxCodes", "battery"]],
	"moisture":			[driver: "Moisture Sensor", selector: "genericMoistures", attr: ["water", "temperature", "battery"]],
	"motion":			[driver: "Motion Sensor", selector: "genericMotions", attr: ["motion", "temperature", "battery"]],
	"multipurpose":		[driver: "Multipurpose Sensor", selector: "genericMultipurposes", attr: ["contact", "temperature", "battery", "acceleration", "threeAxis"]],
	"omnipurpose":		[driver: "Omnipurpose Sensor", selector: "genericOmnipurposes", attr: ["motion", "temperature", "humidity", "illuminance", "ultravioletIndex", "tamper", "battery"]],
	"pocketsocket":		[driver: "Pocket Socket", selector: "pocketSockets", attr: ["switch", "power"]],
	"power":			[driver: "Power Meter", selector: "powerMeters", attr: ["power"]],
	"presence":			[driver: "Presence Sensor", selector: "genericPresences", attr: ["presence", "battery"]],
	"ringdoorbell":		[driver: "Ring Doorbell", selector: "ringDoorbellPros", attr: ["numberOfButtons", "pushed", "motion"]],
	"rgbbulb":			[driver: "RGB Bulb", selector: "genericRGBs", attr: ["switch", "level", "hue", "saturation", "RGB", "color", "colorMode", "colorTemperature"]],
	"shock":			[driver: "Shock Sensor", selector: "genericShocks", attr: ["shock", "battery"]],
	"siren":			[driver: "Siren", selector: "genericSirens", attr: ["switch", "alarm", "battery"]],
	"smartsmoke":		[driver: "Smart Smoke/CO", selector: "smartSmokeCO", attr: ["smoke", "carbonMonoxide", "battery", "temperature", "humidity", "switch", "level", "hue", "saturation", "pressure"]],
	"smoke":			[driver: "Smoke/CO Detector", selector: "genericSmokeCO", attr: ["smoke", "carbonMonoxide", "battery"]],
	"switch":			[driver: "Switch", selector: "genericSwitches", attr: ["switch"]],
	"thermostat":		[driver: "Thermostat", selector: "genericThermostats", attr: ["coolingSetpoint", "heatingSetpoint", "schedule", "supportedThermostatFanModes", "supportedThermostatModes", "temperature", "thermostatFanMode", "thermostatMode", "thermostatOperatingState", "thermostatSetpoint"]],
	"valve":			[driver: "Valve", selector: "genericValves", attr: ["valve"]],
	"windowshade":		[driver: "Window Shade", selector: "windowShades", attr: ["switch", "position", "windowShade"]],
	"zwaverepeater":	[driver: "Iris Z-Wave Repeater", selector: "zwaveRepeaters", attr: ["status", "lastRefresh", "deviceMSR", "lastMsgRcvd"]]
]


// Mapping to receive events
mappings
{
	// Client mappings
    path("/event/:deviceId/:deviceCommand/:commandParams")
	{
		action: [GET: "remoteDeviceCommand"]
	}
    path("/device/:deviceId/sync/:type")
	{
		action: [GET: "getDeviceSync"]
	}
    path("/modes/get")
	{
		action: [GET: "getAllModes"]
	}
    path("/modes/set/:name")
	{
		action: [GET: "serverModeChangeEvent"]
	}
    path("/hsm/set/:name")
	{
		action: [GET: "hsmReceiveEvent"]
	}
    path("/hsm/alert/:text")
	{
		action: [GET: "hsmReceiveAlert"]
	}	
	path("/system/setCommStatus/:status")
	{
		action: [GET: "setCommStatus"]
	}
	path("/system/drivers/save")
	{
		action: [POST: "saveCustomDrivers"]
	}
    path("/system/versions/get")
	{
		action: [GET: "getVersions"]
	}
    path("/system/update")
	{
		action: [GET: "remoteUpdate"]
	}
	
	// Server mappings
    path("/devices/save")
	{
		action: [POST: "saveDevices"]
	}
    path("/device/:deviceId/event/:event")
	{
		action: [GET: "deviceEvent"]
	}
}


/*
	getDeviceSync
    
	Purpose: Retrieves the physical device details and returns them to the controller (main) hub.

	URL Format: GET /device/:deviceId/sync/:type

	Notes: Called by HTTP request from controller hub.
*/
def getDeviceSync()
{
	if (enableDebug) log.info "Received device update request from server: [${params.deviceId}, type ${params.type}]"
	
	def device = getDevice(params)
	if (device != null)
	{
		def currentAttributes = getAttributeMap(device, params.type)	
		def label = device.label ?: device.name
		jsonResponse([status: "success", name: "${device.name}", label: "${label}", currentValues: currentAttributes])
	}
}


/*
	getDevice
    
	Purpose: Helper function to retreive a device from all groups of devices. 
*/
def getDevice(params)
{
	def foundDevice = null

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		if (foundDevice != null) return
		foundDevice = settings."${device.selector}"?.find{it.id == params.deviceId}
	}

	// Custom devices drivers
	if (foundDevice == null)
	{
		state.customDrivers?.each
		{
	 	  groupname, device ->
			if (foundDevice != null) return
			foundDevice = settings."custom_${device.selector}".find{it.id == params.deviceId}
		}
	}
	return foundDevice
}


/*
	remoteDeviceCommand
    
	Purpose: Event handler for remote hub device events.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: Called from HTTP request from server (remote) hub.
*/
def remoteDeviceCommand()
{
	def commandParams = params.commandParams != "null" ? parseJson(URLDecoder.decode(params.commandParams)) : null

	// Get the device
	def device = getDevice(params)
	if (device == null)
	{
		log.error "Could not locate a device with an id of ${device.deviceId}"
		return jsonResponse([status: "error"])
	}
	
	if (enableDebug) log.info "Received command from server: [\"${device.label ?: device.name}\": ${params.deviceCommand}]"
	
	// Make sure the physical device supports the command
	if (!device.hasCommand(params.deviceCommand))
	{
		log.warn "The device [${device.label ?: device.name}] does not support the command ${params.deviceCommand}."
		return jsonResponse([status: "error"])
	}

	// Handle remaining commands
	else if (params.deviceCommand != "")
	{
		switch (commandParams?.size())
		{
			case 1:
				device."${params.deviceCommand}"(commandParams[0])
				break
			case 2:
				device."${params.deviceCommand}"(commandParams[0], commandParams[1])
				break
			case 3:
				device."${params.deviceCommand}"(commandParams[0], commandParams[1], commandParams[2])
				break
			case 4:
				device."${params.deviceCommand}"(commandParams[0], commandParams[1], commandParams[2], commandParams[3])
				break
			default:
				device."${params.deviceCommand}"()
				break
		}
	}

	else
	{
		log.error "Could not locate a device or command."
		return jsonResponse([status: "error"])
	}
	
	jsonResponse([status: "success"])
}


/*
	serverModeChangeEvent
    
	Purpose: Event handler for server (controller) mode change events.

	URL Format: (GET) /modes/set/:name

	Notes: Called from HTTP request from controller hub.
*/
def serverModeChangeEvent()
{
    def modeName = params?.name ? URLDecoder.decode(params?.name) : ""

    if (location.modes?.find{it.name == modeName})
	{
		if (enableDebug) log.debug "Received mode event from server: ${modeName}"
		setLocationMode(modeName)
		jsonResponse([status: "complete"])		
	}
	else
	{
		log.error "Received mode event from server: ${modeName} does not exist!"
		jsonResponse([status: "error"])	
    }
}


/*
	hsmReceiveEvent
    
	Purpose: Event handler for server (controller) HSM status change events.

	URL Format: (GET) /hsm/set/:name

	Notes: Called from HTTP request from server hub.
*/
def hsmReceiveEvent()
{
    def hsmState = params?.name ? URLDecoder.decode(params?.name) : ""

    if (["armAway", "armHome", "armNight", "disarm", "armRules", "disarmRules", "disarmAll", "armAll", "cancelAlerts"].find{it == hsmState})
	{
		if (enableDebug) log.debug "Received HSM event from server: ${hsmState}"
		sendLocationEvent(name: "hsmSetArm", value: hsmState, data: app.id)
		atomicState.lastHSMChange = hsmState
		jsonResponse([status: "complete"])		
	}
	else
	{
		log.error "Received HSM event from server: ${hsmState} does not exist!"
		jsonResponse([status: "error"])	
    }
}


/*
	hsmReceiveAlert
    
	Purpose: Receives HSM alert events from the server hub.

	URL Format: (GET) /hsm/alert/:text

	Notes:Sends an app event with the originating hub and HSM alert message.
*/
def hsmReceiveAlert()
{
	if (!receiveHSM) return
    def hsmAlertText = params?.text ? URLDecoder.decode(params?.text) : ""
	
	sendEvent(app, hsmAlertText)
}


/*
	subscribeLocalEvents
    
	Purpose: Subscribes to all device events for all attribute returned by getSupportedAttributes()

	Notes: 	Thank god this isn't SmartThings, or this would time out after about 10 subscriptions!
*/
def subscribeLocalEvents()
{
	unsubscribe()

	if (state.connectionType == "socket")
	{
		log.info "Skipping event subscriptions...  Using event socket to send events to server."
		return
	}	

	log.info "Subscribing to events.."

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		def selectedDevices = settings."${device.selector}"
		if (selectedDevices?.size()) getSupportedAttributes(groupname).each { subscribe(selectedDevices, it, realtimeEventHandler) }
	}

	// Special handling for Smart Plugs & Power Meters - Kinda Kludgy
	if (!sp_EnablePower && smartPlugs?.size()) unsubscribe(smartPlugs, "power", realtimeEventHandler)
	if (!sp_EnableVolts && smartPlugs?.size()) unsubscribe(smartPlugs, "voltage", realtimeEventHandler)
	if (!pm_EnableVolts && powerMeters?.size()) unsubscribe(powerMeters, "voltage", realtimeEventHandler)
	
	// Custom defined drivers
	state.customDrivers?.each
	{
	  groupname, driver ->
		if (settings."custom_${groupname}"?.size()) getSupportedAttributes(groupname).each { subscribe(settings."custom_${groupname}", it, realtimeEventHandler) }	
	}
}


/*
	realtimeEventHandler
    
	Purpose: Event handler for all local device events.

	URL Format: GET /device/localDeviceId/event/name/value/unit

	Notes: Handles everything from this hub!
*/
def realtimeEventHandler(evt)
{
	if (state.commDisabled) return
	
	def event =
	[
		name:			evt.name,
		value:			evt.value,
		unit:			evt.unit,
		displayName:	evt.device.label ?: evt.device.name,
		data:			evt.data
	]
	
	def data = URLEncoder.encode(JsonOutput.toJson(event), "UTF-8")

	if (enableDebug) log.debug "Sending event to server: ${evt.device.label ?: evt.device.name} [${evt.name}: ${evt.value} ${evt.unit}]"
	sendGetCommand("/device/${evt.deviceId}/event/${data}")
}


/*
	getAttributeMap
    
	Purpose: Returns a map of current attribute values for (device) with the device class (deviceClass).

	Notes: Calls getSupportedAttributes() to obtain list of attributes.
*/
def getAttributeMap(device, deviceClass)
{
	def deviceAttributes = getSupportedAttributes(deviceClass)
	def currentAttributes = []
	deviceAttributes.each
	{
		if (device.supportedAttributes.find{attr -> attr.toString() == it})  // Filter only attributes the device supports
			currentAttributes << [name: "${it}", value: device.currentValue("${it}"), unit: it == "temperature" ? "°"+getTemperatureScale() : it == "power" ? "W" :  it == "voltage" ? "V" : ""]
	}
	return currentAttributes
}


/*
	getSupportedAttributes
    
	Purpose: Returns a list of supported attribute values for the device class (deviceClass).

	Notes: Called from getAttributeMap().
*/
private getSupportedAttributes(deviceClass)
{
	if (NATIVE_DEVICES.find{it.key == deviceClass}) return NATIVE_DEVICES[deviceClass].attr
	if (state.customDrivers.find{it.key == deviceClass}) return state.customDrivers[deviceClass].attr
	return null
}


/*
	realtimeModeChangeHandler
    
	URL Format: GET /modes/set/modeName

	Purpose: Event handler for mode change events on the controller hub (this one).
*/
def realtimeModeChangeHandler(evt)
{
	if (!pushModes) return

	def newMode = evt.value
	if (enableDebug) log.debug "Sending mode change event to server: ${newMode}"
	sendGetCommand("/modes/set/${URLEncoder.encode(newMode)}")
}


/*
	realtimeHSMChangeHandler
    
	URL Format: GET /hsm/set/hsmStateName

	Purpose: Event handler for HSM state change events on the controller hub (this one).
*/
def realtimeHSMChangeHandler(evt)
{
	if (!pushHSM) return

	if (evt?.data?.toInteger() != app.id && atomicState.lastHSMChange != evt.value)
	{
		if (enableDebug) log.debug "Sending HSM state change event to Server: ${evt.value}"
		sendGetCommand("/hsm/set/${URLEncoder.encode(evt.value)}")
		atomicState.lastHSMChange = evt.value
	}
	else if (enableDebug) log.info "Filtering duplicate HSM state change event."
}


/*
	saveDevicesToServer
    
	Purpose: Sends all of the devices selected (& current attribute values) from this hub to the controller hub.

	URL Format: POST /devices/save

	Notes: Makes a single POST request for each group of devices.
*/
def saveDevicesToServer()
{
	if (state.saveDevices == false) return

	// Fetch all devices and attributes for each device group and send them to the master.
	NATIVE_DEVICES.each
	{
	  groupname, device ->

		def devices = []
		settings."${device.selector}".each
		{
			devices << [id: it.id, label: it.label ?: it.name, attr: getAttributeMap(it, groupname)]
		}
		if (devices != [])
		{
			if (enableDebug) log.info "Sending devices to server: ${groupname} - ${devices}"
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: devices])
		}
	}

	// Custom defined device drivers
	state.customDrivers.each
	{
	  groupname, driver ->
		def customSel = settings?."custom_${groupname}"
		if (customSel != null)
		{
			if (enableDebug) log.info "Sending custom devices to server..."
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: customSel])
		}
	}
	state.saveDevices = false
}


/*
	sendDeviceEvent
    
	Purpose: Send an event to a client device.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: CALLED FROM CHILD DEVICE
*/
def sendDeviceEvent(deviceId, deviceCommand, List commandParams=[])
{
	if (state.commDisabled) return

	def dniParts = deviceId.split(":")
	def paramsEncoded = commandParams ? URLEncoder.encode(new groovy.json.JsonBuilder(commandParams).toString()) : null

	sendGetCommand("/event/${dniParts[1]}/${deviceCommand}/${paramsEncoded}")
}


/*
	deviceEvent
    
	Purpose: Handler for events received from physical devices on remote hubs.

	URL Format: (GET) /device/:deviceId/event/:event
*/
def deviceEvent()
{
	def eventraw = params.event ? URLDecoder.decode(params.event) : null
	if (eventraw == null) return

	def event = parseJson(new String(eventraw))
	def data = event?.data ?: ""
	def unit = event?.unit ?: ""

	// We can do this faster if we don't need info on the device
	for (id in state.deviceIdList)
	{
		if (id == params.deviceId)
		{
			sendEvent("${serverIP}:${params.deviceId}", (Map) [name: event.name, value: event.value, unit: unit, descriptionText: "${event.displayName} ${event.name} is ${event.value} ${unit}", isStateChange: true, data: data])
			if (enableDebug) log.info "Received event from server/${event.displayName}: [${event.name}, ${event.value} ${unit}, isStateChange: true]"
			return jsonResponse([status: "complete"])
		}
	}

	if (enableDebug) log.warn "Ignoring Received event from server: Device Not Found!"
	jsonResponse([status: "error"])
}


/*
	saveDevices
    
	Purpose: Save linked devices as received from the remote client hub.

	URL Format: (POST) /devices/save

	Notes: 	Thank god this isn't SmartThings, or this would time out after creating three devices!
*/
def saveDevices()
{	
	// Find the device class
	if (!request?.JSON?.deviceclass || !request?.JSON?.devices)
	{
		return jsonResponse([status: "error"])
	}

	if (NATIVE_DEVICES.find {it.key == request.JSON.deviceclass})
	{
		// Create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "HubConnect ${NATIVE_DEVICES[request.JSON.deviceclass].driver}") }
	}
	else if (state.customDrivers.find {it.key == request.JSON.deviceclass})
	{
		// Get the custom device type and create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "${state.customDrivers[request.JSON.deviceclass].driver}") }		
	}

	// Build a lookup list
	def idList = []
	childDevices.each
	{
		def parts = it.deviceNetworkId.split(":")
		if (parts.size() > 1) idList << parts[1].toString()
	}
	state.deviceIdList = idList

	jsonResponse([status: "complete"])
}


/*
	createLinkedChildDevice
    
	Purpose: Helper function to create child devices. 

	Notes: 	Called from saveDevices()
*/
private createLinkedChildDevice(dev, driverType)
{
	def childDevice = getChildDevices()?.find{it.deviceNetworkId == "${serverIP}:${dev.id}"}
	if (childDevice)
	{
		// Device exists
		if (enableDebug) log.trace "${driverType} ${dev.label} exists... Skipping creation.."
		return
	}
	else
	{
		if (enableDebug) log.trace "Creating Device ${driverType} - ${dev.label}... ${serverIP}:${dev.id}..."
		try
		{
			childDevice = addChildDevice("shackrat", driverType, "${serverIP}:${dev.id}", null, [name: dev.label, label: dev.label])
		}
		catch (errorException)
		{
			log.error "... Uunable to create device ${dev.label}: ${errorException}."
			childDevice = null
		}
	}

	// Set the value of the primary attributes
	if (childDevice)
	{
		dev.attr.each
		{
	 	 attribute ->
			childDevice.sendEvent([name: attribute.name, value: attribute.value, unit: attribute.unit])
		}
	}
}


/*
	syncDevice
    
	Purpose: Sync device details with the physcial device by requeting an update of all attribute values from the remote hub.

	Notes: CALLED FROM CHILD DEVICE
*/
def syncDevice(deviceNetworkId, deviceType)
{
	def dniParts = deviceNetworkId.split(":")
	def childDevice = childDevices?.find { it.deviceNetworkId == deviceNetworkId }
	if (childDevice)
	{
		if (enableDebug) log.debug "Requesting device sync from ${clientName}: ${childDevice.label}"

		def data = httpGetWithReturn("/device/${dniParts[1]}/sync/${deviceType}")

		if (data?.status == "success")
		{
			childDevice.setLabel(data.label)
			
			data?.currentValues.each
			{
			  attr ->
				childDevice.sendEvent([name: attr.name, value: attr.value, unit: attr.unit, descriptionText: "Sync: ${childDevice.displayName} ${attr.name} is ${attr.value} ${attr.unit}", isStateChange: true])
			}
		}
	}
}


/*
	httpGetWithReturn
    
	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Command is absolute and must begin with '/'
			Returns JSON Map if successful.
*/
def httpGetWithReturn(command)
{
	def serverURI = state.clientURI + command

	def requestParams =
	[
		uri:  serverURI,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]
    
	httpGet(requestParams)
	{
	  response ->
		if (response?.status == 200)
		{
			return response.data
		}
		else
		{
			log.error "httpGet() request failed with error ${response?.status}"
		}
	}
}


/*
	sendGetCommand
    
	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Executes async http request and does not return data.
*/
def sendGetCommand(command)
{
	def serverURI = state.clientURI + command

	def requestParams =
	[
		uri:  serverURI,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]
    
	asynchttpGet("asyncHTTPHandler", requestParams)
}


/*
	asyncHTTPHandler
    
	Purpose: Helper function to handle returned data from asyncHttpGet.

	Notes: 	Does not return data, only logs errors.
*/
def asyncHTTPHandler(response, data)
{
	if (response?.status != 200)
	{
		log.error "httpGet() request failed with error ${response?.status}"
	}
}


/*
	sendPostCommand
    
	Purpose: Helper function to format POST requests with the proper oAuth token.

	Notes: 	Returns JSON Map if successful.
*/
def sendPostCommand(command, data)
{
	def serverURI = state.clientURI + command + "?access_token=" + state.clientToken

	def requestParams =
	[
		uri:  serverURI,
		requestContentType: "application/json",
		body: data
	]

	httpPostJson(requestParams)
	{
	  response ->
		if (response?.status == 200)
		{
			return response.data
		}
		else
		{
			log.error "httpPost() request failed with error ${response?.status}"
		}
	}
}


/*
	appHealth
    
	Purpose: Checks in with the controller hub every 1 minute.

	URL Format: /ping

	Notes: 	Hubs are considered in a warning state after missing 2 pings (2 minutes).
			Hubs are considered offline after missing 5 pings (5 minutes).
			When a hub is offline, the virtual hub device presence state will be set to "not present".
*/
def appHealth()
{
	sendGetCommand("/ping")
}


/*
	setCommStatus
    
	Purpose: Event handler which disables events communications between hubs.

	Notes: 	This is useful if the coordinator has to be rebooted to prevent HTTP errors on the remote hubs.
*/
def setCommStatus()
{
	log.info "Received setCommStatus command from server: disabled ${params.status}]"
	state.commDisabled = params.status == "false" ? false : true
	jsonResponse([status: "success", switch: params.status == "false" ? "on" : "off"])
}


/*
	getAllModes
    
	Purpose: Returns a list of all configured modes.

	URL Format: (GET) /modes/get

	Notes: Called from HTTP request from controller hub.
*/
def getAllModes()
{
	jsonResponse(modes: location.modes, active: location.mode)
}


/*
	saveCustomDrivers
    
	Purpose: Saves custom drivers defined in server app to this client instance

	Notes: Sent from server hub.
*/
def saveCustomDrivers()
{
	if (request?.JSON?.find{it.key == "customdrivers"})
	{
		// Clean up
		state.customDrivers.each
		{
	  	  key, driver ->
			if (!request?.JSON?.customdrivers?.find{it == key})
			{
				if (logDebug) log.debug "Removing custom device selector: ${key}"
				unsubscribe(settings."custom_${key}")
				app.removeSetting("custom_${key}")
			}
		}
		state.customDrivers = request.JSON.customdrivers
		jsonResponse([status: "success"])
	}
	else
	{
		jsonResponse([status: "error"])
	}
}


/*
	installed
    
	Purpose: Standard install function.

	Notes: Doesn't do much.
*/
def installed()
{
	log.info "${app.name} Installed"

	state.saveDevices = false
	state.installedVersion = appVersion

	initialize()
}


/*
	updated
    
	Purpose: Standard update function.

	Notes: Still doesn't do much.
*/
def updated()
{
	log.info "${app.name} Updated"

	if (state?.customDrivers == null)
	{
		state.customDrivers = [:]
	}

	initialize()
	state.installedVersion = appVersion
}
def remoteUpdate(params) { updated(); jsonResponse([status: "success"]) }


/*
	initialize
    
	Purpose: Initialize the server instance.

	Notes:Gets things ready to go!
*/
def initialize()
{
	log.info "${app.name} Initialized"
	unschedule()

   	state.commDisabled = false

	// Build a lookup list
	def idList = []
	childDevices.each
	{
		def parts = it.deviceNetworkId.split(":")
		if (parts.size() > 1) idList << parts[1].toString()
	}
	state.deviceIdList = idList

	if (isConnected)
	{
		saveDevicesToServer()
		subscribeLocalEvents()
		if (pushModes) subscribe(location, "mode", realtimeModeChangeHandler)
		if (pushHSM) subscribe(location, "hsmSetArm", realtimeHSMChangeHandler)
		runEvery1Minute("appHealth")
	}
    
	state.saveDevices = false
}


/*
	jsonResponse
    
	Purpose: Helper function to render JSON responses
*/
def jsonResponse(respMap)
{
	render contentType: 'application/json', data: JsonOutput.toJson(respMap)
}


/*
	getDevicePageStatus
    
	Purpose: Helper function to set flags for configured devices.
*/
def getDevicePageStatus()
{
	def status =
	[
		sensorsPage: genericContacts?.size() ?: genericMultipurposes?.size() ?: genericOmnipurposes?.size() ?: genericMotions?.size() ?: genericShocks?.size(),
		shackratsDriverPage: smartPlugs?.size() ?: zwaveRepeaters?.size(),
		switchDimmerBulbPage: genericSwitches?.size() ?: genericDimmers?.size() ?: genericRGBs?.size() ?: pocketSockets?.size() ?: energyPlugs?.size() ?: powerMeters?.size() ?: fanControl?.size(),
		safetySecurityPage: genericSmokeCO?.size() ?: smartSmokeCO?.size() ?: genericMoistures?.size() ?: genericKeypads?.size() ?: genericLocks?.size() ?: genericSirens?.size(),
		otherDevicePage: genericPresences?.size() ?: smartThingsArrival?.size() ?: genericButtons?.size() ?: genericThermostats?.size() ?: genericValves?.size() ?: garageDoors?.size() ?: windowShades?.size()
	]
	status << [all: status.sensorsPage ?: status.shackratsDriverPage ?: status.switchDimmerBulbPage ?: status.safetySecurityPage ?: status.otherDevicePage]
}


/*
	mainPage
    
	Purpose: Displays the main (landing) page.

	Notes: 	Not very exciting.
*/
def mainPage()
{
	if (isConnected && state.installedVersion != appVersion) return upgradePage()

	dynamicPage(name: "mainPage", uninstall: true, install: true)
	{
		section("<h2>${app.label}</h2>"){}
		section("-= <b>Main Menu</b> =-")
		{
			href "connectPage", title: "Connect to Server Hub...", description: "", state: isConnected ? "complete" : null
			if (isConnected) href "devicePage", title: "Select devices to synchronize to Server hub...", description: "", state: devicePageStatus.all ? "complete" : null
			input "pushModes", "bool", title: "Push mode changes to Server Hub?", description: "", defaultValue: false
			input "pushHSM", "bool", title: "Send HSM changes to Server Hub?", description: "", defaultValue: false
		}
		section("-= <b>Debug Menu</b> =-")
		{
			input "enableDebug", "bool", title: "Enable debug output?", required: false, defaultValue: false
		}
		section("-= <b>HubConnect v${appVersion.major}.${appVersion.minor}</b> =-")
		{
			href "aboutPage", title: "Help Support HubConnect!", description: "HubConnect is provided free of charge for the benefit the Hubitat community.  If you find HubConnect to be a valuable tool, please help support the project."
			paragraph "<span style=\"font-size:.8em\">Remote Client v${appVersion.major}.${appVersion.minor}.${appVersion.build} ${appCopyright}</span>"
		}
	}
}


/*
	upgradePage
    
	Purpose: Displays the splash page to force users to initialize the app after an upgrade.
*/
def upgradePage()
{
	dynamicPage(name: "upgradePage", uninstall: false, install: true)
	{
		section("New Version Detected!")
		{
			paragraph "<b style=\"color:green\">This HubConnect Remote Client has an upgrade that has been installed...</b> <br /> Please click [Done] to complete the installation."
		}
	}
}


/*
	connectPage
    
	Purpose: Displays the local & remote oAuth links.

	Notes: 	Really should create a proper token exchange someday.
*/
def connectPage()
{
	if (!state?.accessToken)
	{
		createAccessToken()
	}

	def responseText = ""
	if (serverKey)
	{
		def accessData
		try
		{
			accessData = parseJson(new String(serverKey.decodeBase64()))
		}
		catch (errorException)
		{
			log.error "Error reading connection key: ${errorException}."
			responseText = "Error: Corrupt or invalid connection key"
			state.connected = false
            accessData = null
		}
		if (accessData && accessData?.token && accessData?.type != "smartthings")
		{
			// Set the coordinator hub details
			state.clientURI = accessData.uri
			state.clientToken = accessData.token
			state.clientType = accessData.type
			state.connectionType = accessData.connectionType
			
			// Send our connect string to the coordinator
			def connectKey = new groovy.json.JsonBuilder([uri: (state.clientType == "local" ? getFullLocalApiServerUrl() : getFullApiServerUrl()), type: state.clientType, token: state.accessToken, mac: location.hubs[0].name]).toString().bytes.encodeBase64()
			def response = httpGetWithReturn("/connect/${connectKey}")

			if ("${response.status}" == "success")
			{
				state.connected = true
			}
			else
			{
				state.connected = false
				responseText = "<div style=\"color: red\">Error: ${response?.message}</div>"
			}
		}
		else if (accessData?.type == "smartthings") responseText = "<div style=\"color: red\">Error: Connection key is not for this platform</div>"
	}

	// Reset connection data if handshake failed
	if (serverKey == null || disconnectHub || state.connected == false)
	{
		state.clientURI = null
		state.clientToken = null
		state.clientType = null
		state.connectionType = null
		state.connected = false
		if (disconnectHub)
		{
			app.updateSetting("serverKey", [type: "string", value: ""])
			app.updateSetting("disconnectHub", [type: "bool", value: false])
		}
	}

	dynamicPage(name: "connectPage", uninstall: false, install: false)
	{
		section("Server Details")
		{ 
			input "serverIP", "string", title: "Local LAN IP Address of the Server Hub:", required: false, defaultValue: null, submitOnChange: true
			if (serverIP) input "serverKey", "string", title: "Paste the server hub's connection key here:", required: false, defaultValue: null, submitOnChange: true
		}
		section()
		{
			if (state.connected)
			{
				paragraph "<b style=\"color:green\">Connected!</b>"
				input "disconnectHub", "bool", title: "Disconnect Server Hub...", description: "This will erase the connection key.", required: false, submitOnChange: true
			}
			else paragraph "<b style=\"color:red\">Not Connected</b>${responseText}"
		}
	}
}


/*
	devicePage
    
	Purpose: Displays the page where devices are selected to be linked to the controller hub.

	Notes: 	Really could stand to be better organized.
*/
def devicePage()
{
	def totalNativeDevices = 0
	def requiredDrivers = ""
	NATIVE_DEVICES.each
	{devicegroup, device ->
		if (settings."${device.selector}"?.size())
		{
			totalNativeDevices += settings."${device.selector}"?.size()
			requiredDrivers += "<li>HubConnect ${device.driver}</li>"
		}
	}

	def totalCustomDevices = 0
	state.customDrivers?.each
	{devicegroup, device ->
		totalCustomDevices += settings."${device.selector}"?.size() ?: 0
	}
	
	def totalDevices = totalNativeDevices + totalCustomDevices

	dynamicPage(name: "devicePage", uninstall: false, install: false)
	{
		section("<b> Select Devices to Link to Coordinator Hub </b>  (${totalDevices} connected)")
		{ 
			href "sensorsPage", title: "Sensors", description: "Contact, Motion, Multipurpose, Omnipurpose, Shock", state: devicePageStatus.sensorsPage ? "complete" : null
			href "shackratsDriverPage", title: "Shackrat's Drivers", description: "Iris Smart Plug, Z-Wave Repeaters", state: devicePageStatus.shackratsDriverPage ? "complete" : null
			href "switchDimmerBulbPage", title: "Switches, Dimmers, & Fans", description: "Switch, Dimmer, Bulb, Power Meters", state: devicePageStatus.switchDimmerBulbPage ? "complete" : null
			href "safetySecurityPage", title: "Safety & Security", description: "Locks, Keypads, Smoke & Carbon Monoxide, Leak, Sirens", state: devicePageStatus.safetySecurityPage ? "complete" : null
			href "otherDevicePage", title: "Other Devices", description: "Presence, Button, Valves, Garage Doors, Window Shades", state: devicePageStatus.otherDevicePage ? "complete" : null
			href "customDevicePage", title: "Custom Devices", description: "Devices with user-defined drivers.", state: totalCustomDevices ? "complete" : null
		}
		if (requiredDrivers?.size())
		{
			section("<b>-= Required Drivers =-</b>")
			{
				paragraph "Please make sure the following native drivers are installed on the Coordinator (master) hub before clicking \"Done\": <ul>${requiredDrivers}</ul>" 
			}
		}
	}
}


/*
	sensorsPage
    
	Purpose: Displays the page where sensor-type (motion, contact, etc.) devices are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def sensorsPage()
{
	state.saveDevices = true

	dynamicPage(name: "sensorsPage", uninstall: false, install: false)
	{
		section("<b>-= Select Contact Sensors (${genericContacts?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericContacts", "capability.contactSensor", title: "Contact Sensors (contact):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Multipurpose Sensors (${genericMultipurposes?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericMultipurposes", "capability.accelerationSensor", title: "Contact Multipurpose (contact, acceleration, threeAxis):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Omnipurpose Sensors (${genericOmnipurposes?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericOmnipurposes", "capability.relativeHumidityMeasurement", title: "Generic Omni-Sensor (contact, temperature, humidity, illuminance):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Motion Sensors (${genericMotions?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericMotions", "capability.motionSensor", title: "Motion Sensors (motion, temperature):", required: false, multiple: true, defaultValue: null
			input "irisV3Motions", "capability.motionSensor", title: "Motion Sensors (motion, temperature, humidity):", required: false, multiple: true, defaultValue: null
			input "domeMotions", "capability.motionSensor", title: "Motion Sensors (motion, temperature, illuminance):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Shock Sensors (${genericShocks?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericShocks", "capability.shockSensor", title: "Glass Break/Shock Sensors (shock):", required: false, multiple: true, defaultValue: null
		}
	}
}


/*
	shackratsDriverPage
    
	Purpose: Displays the page where shackrat's custom device drivers (SmartPlug, Z-Wave Repeater) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def shackratsDriverPage()
{
	state.saveDevices = true

	dynamicPage(name: "shackratsDriverPage", uninstall: false, install: false)
	{
		section("<b>-= Select Smart Plug Devices (${smartPlugs?.size() ?: "0"} connected) =- </b>")
		{ 
			input "smartPlugs", "device.IrisSmartPlug", title: "Iris Smart Plugs (switch, power, voltage):", required: false, multiple: true, defaultValue: null
			input "sp_EnablePower", "bool", title: "Enable power meter (W) reporting?", required: false, defaultValue: true
			input "sp_EnableVolts", "bool", title: "Enable voltage (V) reporting?", required: false, defaultValue: true
		}
		section("<b>-= Select Z-Wave Plus Repeater Devices (${zwaveRepeaters?.size() ?: "0"} connected) =- </b>")
		{ 
			input "zwaveRepeaters", "device.IrisZ-WaveRepeater", title: "Z-Wave Repeaters (status):", required: false, multiple: true, defaultValue: null
		}
	}
}


/*
	switchDimmerBulbPage
    
	Purpose: Displays the page where switches, dimmers, bulbs, and power meters are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def switchDimmerBulbPage()
{
	state.saveDevices = true

	dynamicPage(name: "switchDimmerBulbPage", uninstall: false, install: false)
	{
		section("<b>-= Select Switch Devices (${genericSwitches?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericSwitches", "capability.switch", title: "Generic Switches (switch):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Dimmer Devices (${genericDimmers?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericDimmers", "capability.switchLevel", title: "Generic Dimmers (switch, level):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select RGB Bulbs (${genericRGBs?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericRGBs", "capability.colorControl", title: "Generic RGB Bulbs (switch, level, hue, saturation):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Zigbee Plugs/Pocket Sockets (${pocketSockets?.size() ?: "0"} connected) =-</b>")
		{ 
			input "pocketSockets", "capability.switch", title: "Pocket Sockets (switch, power):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Smart Plug Devices (${energyPlugs?.size() ?: "0"} connected) =- </b>")
		{ 
			input "energyPlugs", "capability.energyMeter", title: "Aeon/Dome Smart Plugs (switch, power, voltage, energy, acceleration, current):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Power Meter Devices (${powerMeters?.size() ?: "0"} connected) =-</b>")
		{ 
			input "powerMeters", "capability.powerMeter", title: "Power Meter Devices (power, voltage):", required: false, multiple: true, defaultValue: null
			input "pm_EnableVolts", "bool", title: "Enable voltage reporting?", required: false, defaultValue: true
		}
		section("<b>-= Select Fan Devices (${fanControl?.size() ?: "0"} connected) =-</b>")
		{ 
			input "fanControl", "capability.fanControl", title: "Fan Controller Devices (speed):", required: false, multiple: true, defaultValue: null
		}
	}
}


/*
	safetySecurityPage
    
	Purpose: Displays the page where safety & security devices (smoke, carbonMonoxide, sirens, etc.) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def safetySecurityPage()
{
	state.saveDevices = true

	dynamicPage(name: "safetySecurityPage", uninstall: false, install: false)
	{
		section("<b>-= Select Smoke and CO Detectors (${genericSmokeCO?.size() ?: "0"} connected) =-</b>")
		{
			input "genericSmokeCO", "capability.smokeDetector", title: "Smoke and CO Detectors (smoke, carbonMonoxide):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Smart Smoke and CO Detectors (${smartSmokeCO?.size() ?: "0"} connected) =-</b>")
		{
			input "smartSmokeCO", "device.HaloSmokeAlarm", title: "Halo Smoke and CO Detectors (smoke, carbonMonoxide, temperature, humidity, switch, level, pressure):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Moisture Sensors (${genericMoistures?.size() ?: "0"} connected) =-</b>")
		{
			input "genericMoistures", "capability.waterSensor", title: "Moisture Sensors (water):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Keypads (${genericKeypads?.size() ?: "0"} connected) =-</b>")
		{
			input "genericKeypads", "capability.securityKeypad", title: "Keypads (motion, temperature, tamper, alarm):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Locks (${genericLocks?.size() ?: "0"} connected) =-</b>")
		{
			input "genericLocks", "capability.lock", title: "Locks (lock):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Sirens (${genericSirens?.size() ?: "0"} connected) =-</b>")
		{
			input "genericSirens", "capability.alarm", title: "Sirens (switch, alarm):", required: false, multiple: true, defaultValue: null
		}
	}
}


/*
	otherDevicePage
    
	Purpose: Displays the page where other devices (presence, valves, etc.) are selected to be linked to the controller hub.

	Notes: 	First attempt at organization.
*/
def otherDevicePage()
{
	state.saveDevices = true

	dynamicPage(name: "otherDevicePage", uninstall: false, install: false)
	{
		section("<b>-= Select SmartThings Arrival Sensors (${smartThingsArrival?.size() ?: "0"} connected) =-</b>")
		{
			input "smartThingsArrival", "capability.presenceSensor", title: "SmartThings Arrival Sensors (presence, tone):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Presence Sensors (${genericPresences?.size() ?: "0"} connected) =-</b>")
		{
			input "genericPresences", "capability.presenceSensor", title: "Presence Sensors (presence, alarm):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Button Devices (${genericButtons?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericButtons", "capability.pushableButton", title: "Buttons (pushed, held, doubleTapped, released):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Thermostat Devices (${genericThermostats?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericThermostats", "capability.thermostat", title: "Thermostats (coolingSetpoint, heatingSetpoint, schedule, supportedThermostatFanModes, supportedThermostatModes, temperature, thermostatFanMode, thermostatMode, thermostatOperatingState, thermostatSetpoint):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Water Valve Devices (${genericValves?.size() ?: "0"} connected) =-</b>")
		{ 
			input "genericValves", "capability.valve", title: "Water Valves (valve):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Garage Doors (${garageDoors?.size() ?: "0"} connected) =-</b>")
		{
			input "garageDoors", "capability.garageDoorControl", title: "Garage Doors (door):", required: false, multiple: true, defaultValue: null
		}
		section("<b>-= Select Window Shades (${windowShades?.size() ?: "0"} connected) =-</b>")
		{
			input "windowShades", "capability.windowShade", title: "Window Shades:", required: false, multiple: true, defaultValue: null
		}
	}
}


/*
	customDevicePage
    
	Purpose: Displays the page where custom (user-defined) devices are selected to be linked to the controller hub.

	Notes: 	First attempt at remotely defined device definitions.
*/
def customDevicePage()
{
	state.saveDevices = true

	dynamicPage(name: "customDevicePage", uninstall: false, install: false)
	{
		state.customDrivers.each
		{
		  groupname, driver ->
			def customSel = settings."custom_${groupname}"
			section("<b>-= Select ${driver.driver} Devices (${customSel?.size() ?: "0"} connected) =-</b>")
			{
				input "custom_${groupname}", "capability.${driver.selector}", title: "${driver.driver} Devices (${driver.attr}):", required: false, multiple: true, defaultValue: null
			}
		}
	}
}


/*
	aboutPage

	Purpose: Displays the about page with credits.
*/
def aboutPage()
{
	dynamicPage(name: "aboutPage", title: "HubConnect v${appVersion.major}.${appVersion.minor}", uninstall: false, install: false)
	{
		section()
		{
			paragraph "HubConnect is provided free for personal and non-commercial use.  Countless hours went into the development and testing of this project.  If you like it and would like to see it succeed, or see more apps like this in the future, please consider making a small donation to the cause."
			href "donate", style:"embedded", title: "Please consider making a \$20 or \$40 donation to show your support!", image: "http://irisusers.com/hubitat/hubconnect/donate-icon.png", url: "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=T63P46UYH2TJC&source=url"
		}
		section()
		{
			href "mainPage", title: "Home", description: "Return to HubConnect main menu..."
			paragraph "<span style=\"font-size:.8em\">Remote Client v${appVersion.major}.${appVersion.minor}.${appVersion.build} ${appCopyright}</span>"
		}
	}
}


/*
	getVersions

	URL Format: (GET) /system/versions/get

	Purpose: Returns Remote Client & Active driver versions to server container.
*/
def getVersions()
{
	// Get hub app & drivers
	def remoteDrivers = [:]
	getChildDevices()?.each
	{
	   device ->
		if (remoteDrivers[device.typeName] == null) remoteDrivers[device.typeName] = device.getDriverVersion()
	}
	jsonResponse([apps: [[appName: app.label, appVersion: appVersion]], drivers: remoteDrivers])
}

def getIsConnected(){(state?.clientURI?.size() > 0 && state?.clientToken?.size() > 0) ? true : false}
def getAppVersion() {[platform: "Hubitat", major: 1, minor: 4, build: 2]}
def getAppCopyright(){"&copy; 2019 Steve White, Retail Media Concepts LLC <a href=\"https://github.com/shackrat/Hubitat-Private/blob/master/HubConnect/License%20Agreement.md\" target=\"_blank\">HubConnect License Agreement</a>"}