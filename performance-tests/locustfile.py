from geventhttpclient.client import HTTPClientPool
from locust import FastHttpUser, task, constant_pacing, tag
import random


def generate_thought_data():
    """Generate random thought data for testing"""
    return {
        "content": f"Test thought {random.randint(0, 10000000000000000000000000000000000000000000)}",
        "kind": "POSITIVE"
    }


class ThoughtUser(FastHttpUser):
    """
    User class that simulates interactions with the Thoughts API
    Uses FastHttpUser for better performance
    """
    host = "http://localhost:8080"
    wait_time = constant_pacing(1.0)
    client_pool = HTTPClientPool(concurrency=50)

    @tag('save')
    @task(1)
    def save_thought(self):
        """Test saving a thought"""
        data = generate_thought_data()
        with self.client.post("/thoughts", json=data, catch_response=True) as response:
            if response.status_code != 200:
                response.failure("Fail")

    @tag('get-all')
    @task(1)
    def get_all_thoughts(self):
        """Test getting all thoughts"""
        with self.client.get("/thoughts", catch_response=True) as response:
            if response.status_code != 200:
                response.failure("Fail")
