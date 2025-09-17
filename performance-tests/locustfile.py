import logging

from geventhttpclient.client import HTTPClientPool
from locust import FastHttpUser, task, constant_pacing, tag, events
import random

from locust.runners import WorkerRunner, MasterRunner


def generate_thought_data():
    """Generate random thought data for testing"""
    return {
        "content": f"Test thought {random.randint(0, 10000000000000000000000000000000000000000000)}",
        "kind": "POSITIVE"
    }


@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """
    This listener runs on the master and on each worker.
    We check the runner type to see where we are.
    """
    if isinstance(environment.runner, WorkerRunner):
        # This code will only run on a worker node
        worker_id = environment.runner.client_id
        logging.info(f"--- Test started on WORKER with ID: {worker_id} ---")

    elif isinstance(environment.runner, MasterRunner):
        # This code will only run on the master node
        logging.info("--- Test started on MASTER node ---")

    else:
        # This code will run when in standalone mode (not distributed)
        logging.info("--- Test started in STANDALONE mode ---")

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
