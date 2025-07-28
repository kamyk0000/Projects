import Priority


class Task:
    def __init__(self, description: str, priority: Priority, completed: bool = False):
        self.description = description
        self.completed = completed
        self.priority = priority

    def __str__(self):
        return f"{self.description}"
