package neumatica.location.service_location_neumatica.record;

public record RegisterCheckoutRequest(
		
		Double departureLatitude,
	    Double departureLongitude,
	    Double precisionGpsOutput) {

}
