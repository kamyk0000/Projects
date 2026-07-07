extends Node3D
class_name MySphere


@export var throwStrength = 0.0
@export var throwDirection = Vector3.ZERO


@export var bounciness: float = 0.1
@export var drag: float = 0.5
@export var damping: float = 1
@export var radius: float = 0
var velocity = Vector3.ZERO
var inverse_inertia_tensor


@export var angular_velocity = Vector3.ZERO
@export var mass: float = 1.0
@export var angular_damping: float = 0.99
var inverse_inertia = Vector3.ONE
var inverse_mass: float


@export var sleep_threshold = 0.05
@export var is_static: bool = false
var is_resting: bool
var will_be_resting = false
var sleep_timer = 0.0
var sleeping: bool = false


var sleep_vel_threshold = 0.05
var sleep_time_threshold = 0.5
var resting_face = -1
var resting_time = 0.0
var resting_basis = Basis()


@export var axis_length = 3.0
@onready var mesh: MeshInstance3D = $MeshInstance3D


func _ready() -> void :
	if radius <= 0.0:
		radius = mesh.scale.x / 2
	else:
		mesh.scale = Vector3(radius * 2, radius * 2, radius * 2)
	inverse_mass = 1.0 / mass
	if mass == 0: sleeping = true
	calculate_inertia()
	print(self, " inertia: ", inverse_inertia_tensor)

	MyPhysics.collidable_objects.append(self)
	pass


func _process(delta: float) -> void :
	if global_position.y < -1:
		queue_free()

func wake_up():
	if not sleeping or mass == 0: return
	sleeping = false
	sleep_timer = 0
	resting_face = -1
	print(self, " Im Awake")

func calculate_inertia():
	if mass == 0:
		inverse_inertia_tensor = Basis(
			Vector3.ZERO, 
			Vector3.ZERO, 
			Vector3.ZERO
		)
		return

	var i = 1.0 / (2.0 / 5.0) * mass * radius * radius
	inverse_inertia_tensor = Basis(
		Vector3(1.0 / i, 0, 0), 
		Vector3(0, 1.0 / i, 0), 
		Vector3(0, 0, 1.0 / i)
	)

func calculate_obb():
	global_transform.basis.x
	global_transform.basis.y
	global_transform.basis.z

func throw(strength: float, direction: Vector3) -> void :
	velocity = direction.normalized() * strength
	wake_up()
