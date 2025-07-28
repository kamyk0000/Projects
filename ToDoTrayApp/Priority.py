class Priority:
    def __init__(self, index: int, name: str, color):
        self.index = index
        self.name = name
        self.color = color

    def __str__(self):
        return f"{self.name}"
