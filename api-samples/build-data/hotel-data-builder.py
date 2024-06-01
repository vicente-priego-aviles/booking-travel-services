import json
import random
import requests

def write_random_hotels(num_hotels):
    hotel_names = ["Grand Hotel Centro", "Luxury Stay Downtown", "Budget Inn Riverside"]
    directions = [
        "Calle Falsa Centro 123, Torrevieja 03183 - Alicante",
        "123 Luxury St, Downtown 45678 - Metropolis",
        "456 Riverside Ave, Riverside 78901 - Springfield"
    ]
    room_titles = ["Habitacion individual", "Habitacion doble", "Suite"]
    people_capacities = [1, 2, 4]

    hotels_array = []

    for _ in range(num_hotels):
        name = random.choice(hotel_names)
        direction = random.choice(directions)
        cost_per_night = random.randint(5000, 10000)  # Random cost between 5000 and 10000

        rooms_array = []
        for i in range(random.randint(1, 5)):  # Each hotel has between 1 to 5 types of rooms
            title = f"{random.choice(room_titles)} {chr(65+i)}"  # A, B, C, etc.
            people_capacity = random.choice(people_capacities)
            availabilities = [{
                "startDate": 1000000000000,
                "endDate": 3000000000000
            }]

            room_object = {
                "title": title,
                "peopleCapacity": people_capacity,
                "availabilities": availabilities
            }
            rooms_array.append(room_object)

        hotel_object = {
            "name": name,
            "direction": direction,
            "costPerNight": cost_per_night,
            "rooms": rooms_array
        }

        hotels_array.append(hotel_object)

    return hotels_array

# Open the file
for _ in range(0, 1000):  # Assuming you want to send 100 batches
    hotels_list = write_random_hotels(10)  # Generate 10 random hotels
    hotels_json = json.dumps(hotels_list)

    headers = {
        'Content-Type': 'application/json'
    }
    response = requests.post("http://localhost:9191/hotel-service/api/hotels-booking", headers=headers, data=hotels_json)
    print("Iteration:", _,". Response received. Status code = ", response.status_code)
    if response.status_code != 201:
        exit(-1)

print("Requests finished correctly'.")