import tkinter as tk
from tkinter import messagebox

from Task import Task


""" Positions the window (either to {slightly offset} bottom right or center """
def position_window(window, position):
    window.update_idletasks()
    width = window.winfo_width()
    height = window.winfo_height()
    screen_width = window.winfo_screenwidth()
    screen_height = window.winfo_screenheight()

    if position == "center":
        x = (screen_width // 2) - (width // 2)
        y = (screen_height // 2) - (height // 2)
    elif position == "bottom_right":
        x = screen_width - width
        y = screen_height - height - 40
    else:
        x = 0
        y = 0

    window.geometry(f'{width}x{height}+{x}+{y}')


bg = "#171717"
fg = "#e6e6e6"
mf = ("Helvetica", 12)
lf = ("Helvetica", 14, "bold")
wl = 135


""" Handles Task objects """
class TaskManager:
    def __init__(self, root, tasks, tasks_frame):
        self.add_task_window = None
        self.priorities = []
        self.root = root
        self.tasks = tasks
        self.tasks_frame = tasks_frame

    def get_tasks(self):
        return self.tasks

    """ Builds and sorts Tasks on the tasks frame (list) """
    def refresh_tasks(self):
        for widget in self.tasks_frame.winfo_children():
            widget.destroy()
        # Task sorting by priority, then completion, then alphabetical
        self.tasks.sort(key=lambda x: (x.priority.index, x.completed, x.description))
        current_priority = None
        # Task display
        for task in self.tasks:
            # Checking for new priority
            if task.priority != current_priority:
                current_priority = task.priority
                # Priority label
                tk.Label(self.tasks_frame, text=current_priority, font=lf, wraplength=wl, bg=current_priority.color,
                         width=15).pack(fill="x", expand=True)
            # Task frame
            task_frame = tk.Frame(self.tasks_frame, bg=bg)
            task_frame.pack(fill='x', pady=0, expand=True)
            # Priority strip
            tk.Label(task_frame, width=1, bg=current_priority.color).pack(side='left', fill='y')
            # Task checkbox
            task_var = tk.BooleanVar(value=task.completed)
            (tk.Checkbutton(task_frame, bg=bg, variable=task_var, onvalue=True, offvalue=False,
                            command=lambda t=task, var=task_var: self.update_task_completion(t, var))
             .pack(side='left', anchor='w', ))
            # task remove button
            (tk.Button(task_frame, text="-", command=lambda t=task: self.remove_task(t), font=mf, bg=bg, fg=fg)
             .pack(side="right"))
            # Task label
            tk.Label(task_frame, text=task, font=mf, wraplength=wl, bg=bg, fg=fg).pack(fill="x", expand=True)

    """ Checks and removes tasks that have had their priority removed """
    def check_for_removed_priorities(self, priorities):
        self.priorities = priorities
        self.tasks = [task for task in self.tasks if task.priority in priorities]

    def update_task_completion(self, task, var):
        task.completed = var.get()
        self.refresh_tasks()

    def remove_task(self, task):
        self.tasks.remove(task)
        self.refresh_tasks()

    """ Displays add Task window """
    def add_task(self, priorities):
        if self.add_task_window is not None and self.add_task_window.winfo_exists():
            return

        self.priorities = priorities
        self.add_task_window = tk.Toplevel(self.root, bg="white")
        position_window(self.add_task_window, "center")
        self.add_task_window.overrideredirect(True)
        self.add_task_window.attributes("-alpha", 0.8)
        self.add_task_window.geometry("200x100")
        self.add_task_window.title("Add Task")
        # Menu bar
        toolbar = tk.Frame(self.add_task_window, bg="gray", height=1)
        toolbar.pack(side="top", fill="x")
        # Task name field
        task_name_entry = tk.Entry(self.add_task_window)
        task_name_entry.insert(0, "Task name")
        task_name_entry.pack(pady=5)
        # Priorities choice box
        priority_var = tk.StringVar(self.add_task_window)
        priority_var.set(f"1:{self.priorities[0]}")
        options = [f"{i + 1}: {priority}" for i, priority in enumerate(self.priorities)]
        priority_dropdown = tk.OptionMenu(self.add_task_window, priority_var, *options)
        priority_dropdown.pack(pady=5)
        # Menu buttons
        (tk.Button(toolbar, text="+", command=lambda: self.validate_new_task(task_name_entry.get(),
                                                                             self.get_selected_priority(priority_var)),
                   bg="white", width=3, height=1, font=mf).pack(side="left"))
        (tk.Button(toolbar, text="x", command=self.add_task_window.destroy, bg="white", width=3, height=1, font=mf)
         .pack(side="right"))

    """ Returns the selected Priority object from OptionMenu in add_task_window """
    def get_selected_priority(self, selected_option):
        priority_index = int(selected_option.get().split(":")[0]) - 1
        selected_priority = self.priorities[priority_index]
        return selected_priority

    """ Validates the data from add_task() method """
    def validate_new_task(self, description, priority):
        if description is None or description == "" or description == "Task name":
            messagebox.showerror("Input Error", "You must add a name for your task.", parent=self.add_task_window)
            return
        new_task = Task(description, priority)
        self.tasks.append(new_task)
        self.refresh_tasks()
        self.add_task_window.destroy()
