extends Control
class_name UI

@onready var radiusF = $HBoxContainer / RadiusSlider
@onready var massF = $HBoxContainer / MassSlider
@onready var radiusL = $HBoxContainer / RadiusLabel
@onready var massL = $HBoxContainer / MassLabel
@onready var scoreL = $ScoreLabel

var is_dragging: = false
var start_pos: = Vector2.ZERO
var start_time: = 0.0
static var score = 0

func _ready() -> void :
	pass

func _process(delta: float) -> void :
	scoreL.text = str("Score: ", score)
	pass

static func update_score(val):
	score += val

func _input(event):
	if event is InputEventMouseButton:

		if event.button_index == MOUSE_BUTTON_LEFT and event.pressed:
			if is_in_allowed_area(event.position):
				return
			is_dragging = true
			start_pos = event.position
			start_time = Time.get_ticks_msec() / 1000.0


		elif event.button_index == MOUSE_BUTTON_LEFT and not event.pressed:
			if is_dragging:
				var end_pos = event.position
				var end_time = Time.get_ticks_msec() / 1000.0
				handle_sling(start_pos, end_pos, start_time, end_time)
				is_dragging = false

func is_in_allowed_area(pos: Vector2) -> bool:
	var screen_height = get_viewport().get_visible_rect().size.y
	var cutoff = screen_height * 0.8
	return pos.y > cutoff

func handle_sling(start_pos, end_pos, start_time, end_time):
	var drag_vector = start_pos - end_pos
	var direction = Vector3(drag_vector.x, 0.5, drag_vector.y)

	var distance = drag_vector.length()
	var duration = end_time - start_time
	var strength = distance * duration / 10.0

	MyPhysics.spawn_ball(float(massF.value), float(radiusF.value), strength, direction)

	print("Direction:", direction)
	print("Strength:", strength)

func _on_restart_button_pressed() -> void :
	MyPhysics.restart_scene()
	pass

func _on_mass_slider_value_changed(value: float) -> void :
	massL.text = str("Mass = ", value)
	pass

func _on_radius_slider_value_changed(value: float) -> void :
	radiusL.text = str("Radius = ", value)
	pass
