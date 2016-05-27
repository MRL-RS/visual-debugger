package com.mrl.debugger;


/**
 * User: mrl
 * Date: Apr 28, 2010
 * Time: 11:38:50 PM
 */
public interface MrlConstants {

    String THINK_TIME_KEY = "kernel.agents.think-time";
    String MAX_VIEW_DISTANCE_KEY = "perception.los.max-distance";
    String HP_PRECISION = "perception.los.precision.hp";
    String DAMAGE_PRECISION = "perception.los.precision.damage";
    String MAX_WATER_KEY = "fire.tank.maximum";
    String WATER_REFILL_RATE_KEY = "fire.tank.refill_rate";
    String WATER_REFILL_HYDRANT_RATE_KEY = "fire.tank.refill_hydrant_rate";
    String MAX_EXTINGUISH_DISTANCE_KEY = "fire.extinguish.max-distance";
    String MAX_EXTINGUISH_POWER_KEY = "fire.extinguish.max-sum";
    String MAX_CLEAR_DISTANCE_KEY = "clear.repair.distance";
    String SAY_COMMUNICATION_MODEL_KEY = "kernel.standard.StandardCommunicationModel";
    String SPEAK_COMMUNICATION_MODEL_KEY = "kernel.standard.ChannelCommunicationModel";
    String VOICE_RANGE_KEY = "comms.channels.0.range";
    String MAX_PLATOON_CHANNELS_KEY = "comms.channels.max.platoon";
    String GIS_KEY = "kernel.gis";
    String POSITION_URN = "urn:rescuecore2.standard:property:position";
    String HP_URN = "urn:rescuecore2.standard:property:hp";
    String DAMAGE_URN = "urn:rescuecore2.standard:property:damage";
    String BURIEDNESS_URN = "urn:rescuecore2.standard:property:buriedness";
    String X_URN = "urn:rescuecore2.standard:property:x";
    String Y_URN = "urn:rescuecore2.standard:property:y";


    String ICON_SIZE_KEY = "view.standard.human.icons.size";
    String USE_ICONS_KEY = "view.standard.human.icons.use";
}
