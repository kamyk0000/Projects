import pickle, os, threading
import sys
import tkinter as tk
from tkinter import messagebox
import pystray
from pystray import MenuItem as Item
from PIL import Image, ImageTk
from PriorityManager import PriorityManager
from TaskManager import TaskManager, position_window

DATA_FILE = "toDoData.pkl"


""" Deserializes data with pickle """
def load_data():
    if os.path.exists(DATA_FILE):
        with open(DATA_FILE, 'rb') as file:
            return pickle.load(file)
    return [], []

""" Handles Main Gui """
class App:
    def __init__(self, root):
        self.root = root
        self.root.title("TODO App")
        self.root.geometry("200x300")
        icon = ImageTk.PhotoImage(file="icon/tray_icon.png")
        self.root.iconphoto(False, icon)

        self.create_main_menu()
        self.create_canvas()

        self.root.overrideredirect(True)
        self.root.attributes("-alpha", 0.8)
        self.root.attributes("-topmost", True)
        position_window(self.root, "bottom_right")

        self.priorities, self.tasks = load_data()
        self.p_manager = PriorityManager(self.root, self.priorities)
        self.t_manager = TaskManager(self.root, self.tasks, self.tasks_frame)
        self.t_manager.refresh_tasks()
        self.tray_icon = None

    def create_main_menu(self):
        toolbar = tk.Frame(self.root, bg="gray", height=1)
        toolbar.pack(side="top", fill="x")

        button_font = ("Helvetica", 12)

        tk.Button(toolbar, text="+", command=self.add_task, bg="white", width=3, height=1,
                                    font=button_font).pack(side="left")
        tk.Button(toolbar, text="P+", command=self.manage_priorities, bg="white", width=3,
                                    height=1, font=button_font).pack(side="left")

        tk.Button(toolbar, text="x", command=self.exit_app, bg="white", width=3, height=1,
                                      font=button_font).pack(side="right")
        tk.Button(toolbar, text="_", command=self.minimize_to_tray, bg="white", width=3,
                                         height=1, font=button_font).pack(side="right")

    def create_canvas(self):
        self.canvas = tk.Canvas(self.root, bg="#171717")
        self.tasks_frame = tk.Frame(self.canvas, bg="#171717")
        self.tasks_frame.bind("<Configure>", lambda e: self.canvas.configure(scrollregion=self.canvas.bbox("all")))
        self.canvas.create_window((0, 0), window=self.tasks_frame, anchor="nw")
        self.canvas.pack(side="left", fill="both", expand=True)

        # Bind mouse wheel to scroll the canvas
        self.canvas.bind_all("<MouseWheel>", self.on_mouse_wheel)

    def on_mouse_wheel(self, event):
        # Calculate the current position and the bounds of the scroll region
        current_y = self.canvas.yview()[0]
        scroll_range = self.canvas.bbox("all")[3] - self.canvas.winfo_height()

        # Calculate the new position
        new_y = current_y - (event.delta / 120) * 0.1

        # Clamp the new position to be within the valid range
        new_y = max(0, min(new_y, scroll_range / self.canvas.winfo_height()))

        self.canvas.yview_moveto(new_y)

    def add_task(self):
        if not self.priorities:
            messagebox.showerror("Input Error", "You must add a priority first.", parent=self.root)
            return
        self.t_manager.add_task(self.priorities)

    def manage_priorities(self):
        self.p_manager.show_priorities()
        self.root.wait_window(self.p_manager.priority_window)
        self.priorities = self.p_manager.get_priorities()
        self.t_manager.check_for_removed_priorities(self.priorities)
        self.t_manager.refresh_tasks()
    def minimize_to_tray(self):
        self.root.withdraw()
        self.create_tray_icon()

    def create_tray_icon(self):
        base_dir = os.path.dirname(os.path.abspath(__file__))
        icon_path = os.path.join(base_dir, "icon", "tray_icon.png")

        icon_image = Image.open(icon_path)
        menu = (Item('Restore', self.restore_window), Item('Exit', self.exit_app))
        self.tray_icon = pystray.Icon("TODOListApp", icon_image, "TODO List App", menu)
        self.tray_icon.icon = icon_image
        threading.Thread(target=self.tray_icon.run).start()

    def restore_window(self):
        self.tray_icon.stop()
        self.root.deiconify()
        self.root.attributes("-topmost", True)

    def exit_app(self):
        self.save_data()
        try:
            if self.p_manager.priority_window is not None:
                self.p_manager.priority_window.destroy()
            if self.t_manager.add_task_window is not None:
                self.t_manager.add_task_window.destroy()
            if self.tray_icon is not None:
                self.tray_icon.stop()
        finally:
            self.root.quit()

    """ Serializes data with pickle """
    def save_data(self):
        with open(DATA_FILE, 'wb') as file:
            pickle.dump((self.priorities, self.tasks), file)