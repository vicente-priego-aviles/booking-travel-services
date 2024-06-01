import json
import random
import itertools
import requests

def license_plate_generator():
    letters = itertools.product("ABCDEFGHIJKLMNOPQRSTUVWXYZ", repeat=3)
    numbers = range(1, 10000)  # 0001 to 9999
    for letter_combo in letters:
        for number in numbers:
            yield f"{''.join(letter_combo)}-{number:04d}"

def write_random_cars(num_cars, license_gen):
    brands = ["Mercedes-Benz AMG", "Mercedes-Benz Class", "Mercedes-Benz"]
    models = ["EQS", "S-Class", "E-Class", "C-Class", "GLA", "GLC"]

    objects_array = []

    for _ in range(num_cars):
        brand = random.choice(brands)
        model = random.choice(models)
        license_plate = next(license_gen)
        cost_per_day = round(random.uniform(100, 500), 2)

        car_object = {
            "brand": brand,
            "model": model,
            "license": license_plate,
            "costPerDay": cost_per_day,
            "availabilities": [
                {
                    "startDate": 1000000000000,
                    "endDate": 3000000000000
                }
            ]
        }

        objects_array.append(car_object)

    return objects_array

# Create a license plate generator
license_gen = license_plate_generator()

# Open the file
for _ in range(0, 1000):
    objects_list = write_random_cars(10, license_gen)
    objects_json = json.dumps(objects_list)

    headers = {
        'Content-Type': 'application/json'
    }
    response = requests.post("http://localhost:9191/car-service/api/cars-booking", headers=headers, data=objects_json)
    print("Iteration:", _,". Response received. Status code = ", response.status_code)
    if response.status_code != 201:
        exit(-1)

print("Requests finished correctly'.")