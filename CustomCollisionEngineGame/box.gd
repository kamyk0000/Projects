extends Node3D
class_name MyBox


@export var throwStrength = 0.0
@export var throwDirection = Vector3.ZERO


@export var bounciness: float = 0.1
@export var drag: float = 0.5
@export var damping: float = 1
var velocity = Vector3.ZERO
var half_size: Vector3
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

func _process(delta: float) -> void :
	if global_position.y < -1:
		UI.update_score(10)
		queue_free()

func _ready() -> void :
	half_size = mesh.scale / 2
	inverse_mass = 1.0 / mass
	if mass == 0: sleeping = true
	calculate_inertia()
	print(self, " inertia: ", inverse_inertia_tensor)
	MyPhysics.collidable_objects.append(self)
	pass

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
	var w = mesh.scale.x
	var h = mesh.scale.y
	var d = mesh.scale.z
	var ix = 1.0 / 12.0 * mass * (h * h + d * d)
	var iy = 1.0 / 12.0 * mass * (w * w + d * d)
	var iz = 1.0 / 12.0 * mass * (w * w + h * h)
	inverse_inertia_tensor = Basis(
		Vector3(1.0 / ix, 0, 0), 
		Vector3(0, 1.0 / iy, 0), 
		Vector3(0, 0, 1.0 / iz)
	)

func calculate_obb():
	global_transform.basis.x
	global_transform.basis.y
	global_transform.basis.z

func throw(strength: float, direction: Vector3) -> void :
	velocity = direction.normalized() * strength
