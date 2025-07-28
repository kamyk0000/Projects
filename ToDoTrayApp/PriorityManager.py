import tkinter as tk
from tkinter import simpledialog, messagebox, colorchooser, ttk

from Priority import Priority
from TaskManager import position_window

bg = "#171717"
fg = "#e6e6e6"
mf = ("Helvetica", 12)
lf = ("Helvetica", 16, "bold")
wl = 155


""" Handles Priority objects """
class PriorityManager:
    def __init__(self, root, priorities):
        self.priority_listbox = None
        self.priority_window = None
        self.root = root
        self.priorities = priorities

    def get_priorities(self):
        return self.priorities

    """ Builds the Priority window with a ListBox of Priorities """
    def show_priorities(self):
        if self.priority_window is not None and self.priority_window.winfo_exists():
            return

        self.priority_window = tk.Toplevel(self.root, bg="white")
        position_window(self.priority_window, "center")
        self.priority_window.overrideredirect(True)
        self.priority_window.attributes("-alpha", 0.8)
        self.priority_window.geometry("200x300")
        self.priority_window.title("Priorities List")
        # Menu bar
        toolbar = tk.Frame(self.priority_window, bg="gray", height=1)
        toolbar.pack(side="top", fill="x")
        # Priorities Listbox
        self.priority_listbox = tk.Listbox(self.priority_window, selectmode=tk.SINGLE, font=fg)
        self.priority_listbox.pack(fill=tk.BOTH, expand=True)
        self.refresh_priority_listbox()
        # Menu buttons
        (tk.Button(toolbar, text="+", command=self.add_priority,
                   bg="white", width=3, height=1, font=mf).pack(side="left"))
        (tk.Button(toolbar, text="-", command=self.remove_priority,
                   bg="white", width=3, height=1, font=mf).pack(side="left"))
        (tk.Button(toolbar, text="x", command=self.priority_window.destroy, bg="white", width=3, height=1, font=mf)
         .pack(side="right"))
        # Binding Drag 'N Drop functionalities
        self.priority_listbox.bind('<Button-1>', self.on_drag_start)
        self.priority_listbox.bind('<B1-Motion>', self.on_drag_motion)
        self.priority_listbox.bind('<ButtonRelease-1>', self.on_drag_drop)

    def on_drag_start(self, event):
        self.drag_start_index = self.priority_listbox.nearest(event.y)

    def on_drag_motion(self, event):
        self.priority_listbox.selection_clear(0, tk.END)
        current_index = self.priority_listbox.nearest(event.y)
        self.priority_listbox.selection_set(current_index)

    def on_drag_drop(self, event):
        drag_end_index = self.priority_listbox.nearest(event.y)
        if drag_end_index != self.drag_start_index:
            self.reorder_priorities(self.drag_start_index, drag_end_index)
            self.refresh_priority_listbox()

    """ Reorders Priority importance index based on ListBox order after Drag 'N Drop """
    def reorder_priorities(self, start_index: int, end_index: int):
        self.priorities.insert(end_index, self.priorities.pop(start_index))
        for i, priority in enumerate(self.priorities):
            priority.index = i + 1

    """ Sorts the Priority ListBox items (Priorities) """
    def refresh_priority_listbox(self):
        self.priority_listbox.delete(0, tk.END)
        for priority in self.priorities:
            self.priority_listbox.insert(tk.END, priority)
            self.priority_listbox.itemconfigure(tk.END, background=priority.color, fg=fg)

    """ Displays add Priority window(s) """
    def add_priority(self):
        while True:
            name = simpledialog.askstring("Input", "Enter the priority name:", parent=self.root)
            # User clicked Cancel
            if name is None:
                return
            # Empty input check
            if not name.strip():
                messagebox.showerror("Input Error", "Priority name cannot be empty.", parent=self.root)
                continue
            # Duplicate name check
            if name in self.priorities:
                messagebox.showerror("Input Error", "Choose a different name.", parent=self.root)
                continue
            # User clicked Cancel
            color = colorchooser.askcolor(parent=self.root)[1]
            if color is None:
                return
            break
        index = len(self.priorities) + 1
        new_priority = Priority(index, name, color)
        self.priorities.append(new_priority)
        self.refresh_priority_listbox()

    def remove_priority(self):
        selected_index = self.priority_listbox.curselection()
        if not selected_index:
            return

        confirmed = messagebox.askyesno("Confirmation", "Are you sure you want to remove this priority, "
                                                        "this will result in deleting all tasks of this priority?",
                                        parent=self.priority_window)
        if not confirmed:
            return

        index = selected_index[0]
        del self.priorities[index]
        self.refresh_priority_listbox()
