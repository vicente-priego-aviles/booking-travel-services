import json
import random
import requests

def generate_incremental_reference(prefix, starting_number, increment):
    current_number = starting_number
    while True:
        reference = f"{prefix}{current_number:07d}"
        current_number += increment
        yield reference

def generate_objects(num_objects, reference_generator):
    airport_codes = ["MAD", "BCN", "LHR", "JFK", "CDG", "FRA", "DXB", "LAX", "AMS", "PEK"]
    airport_names = {
        "MAD": "Madrid Barajas",
        "BCN": "Barcelona El Prat",
        "LHR": "London Heathrow",
        "JFK": "New York JFK",
        "CDG": "Paris Charles de Gaulle",
        "FRA": "Frankfurt Airport",
        "DXB": "Dubai International",
        "LAX": "Los Angeles International",
        "AMS": "Amsterdam Schiphol",
        "PEK": "Beijing Capital International"
    }
    objects_list = []

    for _ in range(num_objects):
        departure_code = random.choice(airport_codes)
        arrival_code = random.choice([code for code in airport_codes if code != departure_code])
        reference = next(reference_generator)
        object = {
            "reference": reference,
            "airline": "Iberia",
            "departureAirportCode": departure_code,
            "departureAirportName": airport_names[departure_code],
            "departureTime": 1695024000000,
            "arrivalAirportCode": arrival_code,
            "arrivalAirportName": airport_names[arrival_code],
            "arrivalTime": 1695029400000,
            "cost": 56790,
            "remainingSeats": 5
        }
        objects_list.append(object)
    
    return objects_list

        

# Create a reference generator starting from 456 and incrementing by 1
reference_gen = generate_incremental_reference("X-", 1, 1)
for _ in range(0, 1000):
    object_list = generate_objects(10, reference_gen)
    objects_json = json.dumps(object_list)
    headers = {
        'Content-Type': 'application/json'
    }
    response = requests.post("http://localhost:9191/flight-service/api/flights-booking", headers=headers, data=objects_json)
    print("Iteration:", _,". Response received. Status code = ", response.status_code)
    if response.status_code != 201:
        exit(-1)

print("Flights has been created correctly.")