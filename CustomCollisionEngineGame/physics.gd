extends Node3D
class_name MyPhysics


static var collidable_objects = []


var angular_damping = 0.99
var drag = 0.99
var gravity = 9.81
static var tree



static func restart_scene():
	collidable_objects.clear()
	tree.reload_current_scene()

static func spawn_ball(mass: float, radius: float, strength: float, direction: Vector3):
	var ball = preload("res://Ball.tscn").instantiate()
	ball.mass = mass
	ball.radius = radius
	tree.current_scene.add_child(ball)
	ball.global_position = Vector3(0, 19, 0)
	ball.throw(strength, direction)

func _ready() -> void :
	tree = get_tree()

func get_pairs(arr):
	var result = []
	for i in range(arr.size()):
		for j in range(i + 1, arr.size()):
			result.append([arr[i], arr[j]])
	return result

func _physics_process(delta):
	if collidable_objects.is_empty(): return

	for obj in collidable_objects:
		if !is_instance_valid(obj): continue
		if obj.sleeping: continue
		obj.is_resting = obj.will_be_resting
		obj.will_be_resting = false


		apply_forces(obj, delta)


		apply_velocity(obj, delta)

	var pairs = get_pairs(collidable_objects)
	for pair in pairs:
		var obj_a = pair[0]
		var obj_b = pair[1]
		if !is_instance_valid(obj_a) or !is_instance_valid(obj_b): continue
		if obj_a.sleeping and obj_b.sleeping: continue


		var collision = detect_collision(obj_a, obj_b)
		if not collision["hit"]:
			continue


		apply_impulse(obj_a, obj_b, collision["normal"])


		positional_correction(obj_a, obj_b, collision["normal"], collision["overlap"])


		handle_resting(obj_a, obj_b, collision["normal"])

	for obj in collidable_objects:
		if !is_instance_valid(obj): continue

		update_sleep(obj, delta)


		apply_stabilizing_slerp(obj, delta)



func apply_forces(obj, delta):
	if obj.mass == 0: return

	if not obj.is_resting:
		obj.velocity.y -= gravity * delta
	else:

		if obj.velocity.y < 0:
			obj.velocity.y = 0

	obj.velocity *= pow(drag, delta * 60.0)

	obj.angular_velocity *= pow(angular_damping, delta * 60.0)
	if obj.angular_velocity.length() < 0.05:
		obj.angular_velocity = Vector3.ZERO

func apply_velocity(obj, delta):
	obj.global_position += obj.velocity * delta
	if obj.angular_velocity.length_squared() > 1e-05:
		var axis = obj.angular_velocity / obj.angular_velocity.length()

		obj.rotate_object_local(
			axis, 
			obj.angular_velocity.length() * delta
		)

func detect_collision(obj_a, obj_b):

	if obj_a is MyBox:
		if obj_b is MyBox:
			return collision_box_box(obj_a, obj_b)
		elif obj_b is MySphere:
			return collision_box_sphere(obj_a, obj_b)
	elif obj_a is MySphere:
		if obj_b is MyBox:
			var c = collision_box_sphere(obj_b, obj_a)
			c.normal *= -1
			return c
		elif obj_b is MySphere:
			return collision_sphere_sphere(obj_a, obj_b)
	else:
		return {"hit": false}

func apply_impulse(obj_a, obj_b, normal: Vector3):
	if obj_a.mass == 0 and obj_b.mass == 0:
		return

	var restitution = 0.5


	var contact_point = estimated_contact_point(obj_a, obj_b, normal)


	var ra = contact_point - obj_a.global_position
	var rb = contact_point - obj_b.global_position


	var va = obj_a.velocity + obj_a.angular_velocity.cross(ra)
	var vb = obj_b.velocity + obj_b.angular_velocity.cross(rb)


	var rv = vb - va


	var vel_along_normal = rv.dot(normal)


	if vel_along_normal > -0.1:
		return

	obj_a.wake_up()
	obj_b.wake_up()

	if abs(vel_along_normal) < 0.2:
		restitution = 0.0


	var inv_inertia_a = get_inverse_inertia_world(obj_a)
	var inv_inertia_b = get_inverse_inertia_world(obj_b)


	var ra_cross_n = ra.cross(normal)
	var rb_cross_n = rb.cross(normal)

	var angular_a = (inv_inertia_a * ra_cross_n).dot(ra_cross_n)
	var angular_b = (inv_inertia_b * rb_cross_n).dot(rb_cross_n)

	var angular_term = angular_a + angular_b


	var denom = (
		obj_a.inverse_mass + 
		obj_b.inverse_mass + 
		angular_term)

	denom = max(denom, 1e-06)


	var j = - (1.0 + restitution) * vel_along_normal
	j /= denom


	var impulse = normal * j


	var vt = rv - rv.dot(normal) * normal

	var tangent = Vector3.ZERO
	if vt.length_squared() > 1e-06:
		tangent = vt.normalized()

	var jt = 0.0

	if tangent.length_squared() > 1e-06:
		jt = - rv.dot(tangent)
		jt /= denom

	var mu = 0.6
	jt = clamp(jt, - j * mu, j * mu)

	var friction_impulse = tangent * jt

	if not obj_a.mass == 0:

		obj_a.velocity -= impulse * obj_a.inverse_mass

		obj_a.angular_velocity -= inv_inertia_a * ra.cross(impulse)

		obj_a.velocity -= friction_impulse * obj_a.inverse_mass

	if not obj_b.mass == 0:
		obj_b.velocity += impulse * obj_b.inverse_mass
		obj_b.angular_velocity += inv_inertia_b * rb.cross(impulse)
		obj_b.velocity += friction_impulse * obj_b.inverse_mass

func positional_correction(obj_a, obj_b, normal: Vector3, penetration: float):
	var percent = 0.8
	var slop = 0.01
	var correction_amount = max(penetration - slop, 0.0)
	correction_amount *= percent

	var correction = normal * correction_amount / 2.0

	if obj_a.mass == 0:
		obj_b.position += correction * 1.9
	elif obj_b.mass == 0:
		obj_a.position -= correction * 1.9
	else:
		obj_a.position -= correction
		obj_b.position += correction

func handle_resting(obj_a, obj_b, normal):
	var up = Vector3.UP


	if normal.dot(up) < -0.7:
		obj_a.will_be_resting = true
		if obj_a.velocity.y < 0:
			obj_a.velocity.y = 0



	elif normal.dot(up) > 0.7:
		obj_b.will_be_resting = true
		if obj_b.velocity.y < 0:
			obj_b.velocity.y = 0


func update_sleep(obj, delta):
	if obj.mass == 0 or obj.sleeping:
		return


	if obj.velocity.length() > obj.sleep_vel_threshold\
	or obj.angular_velocity.length() > obj.sleep_vel_threshold:
		obj.sleep_timer = 0.0
		obj.sleeping = false
		return


	if obj.is_resting:
		obj.sleep_timer += delta * 2.0
	else:
		obj.sleep_timer += delta


	if obj.sleep_timer > obj.sleep_time_threshold:
		obj.sleeping = true
		print(obj, " Im Asleep!!")
		obj.velocity = Vector3.ZERO
		obj.angular_velocity = Vector3.ZERO

	if obj.sleeping:
		return












































func apply_stabilizing_slerp(obj, delta):
	if obj.mass == 0:
		return

	if not obj.is_resting:
		obj.resting_face = -1
		obj.resting_time = 0.0
		return

	if obj.velocity.length() > 0.2:
		return

	if obj.resting_face == -1:
		var basis = obj.global_transform.basis

		var faces = [
			basis.x, 
			- basis.x, 
			basis.y, 
			- basis.y, 
			basis.z, 
			- basis.z
		]

		var best_dot = - INF
		var best_index = 0

		for i in range(faces.size()):
			var d = faces[i].dot(Vector3.UP)
			if d > best_dot:
				best_dot = d
				best_index = i

		obj.resting_face = best_index
		obj.resting_basis = obj.global_transform.basis.orthonormalized()

	var basis = obj.global_transform.basis
	var current_up = get_resting_face_normal(obj)
	var target_up = Vector3.UP
	var axis = current_up.cross(target_up)
	var angle = acos(clamp(current_up.dot(target_up), -1.0, 1.0))

	if axis.length() < 0.0001:
		obj.angular_velocity *= 0.8
		return

	axis = axis.normalized()

	var target_rotation = Basis(axis, angle) * basis
	target_rotation = target_rotation.orthonormalized()

	var current = obj.global_transform.basis
	var t = clamp(delta * 6.0, 0.0, 1.0)

	obj.global_transform.basis = current.slerp(target_rotation, t)
	obj.angular_velocity *= 0.85

	if angle < 0.02 and obj.velocity.length() < 0.05:
		obj.global_transform.basis = target_rotation
		obj.angular_velocity = Vector3.ZERO



func collision_sphere_sphere(a, b):
	var delta = b.global_position - a.global_position
	var dist = delta.length()

	var radius_sum = a.radius + b.radius

	if dist >= radius_sum:
		return {"hit": false}

	var normal = delta.normalized()

	if dist == 0:
		normal = Vector3.UP

	var penetration = radius_sum - dist

	return {
		"hit": true, 
		"normal": normal, 
		"overlap": penetration
	}

func collision_box_sphere(box, sphere):
	var closest = closest_point_on_obb(box, sphere.global_position)
	var delta = sphere.global_position - closest
	var dist_sq = delta.length_squared()

	if dist_sq > sphere.radius * sphere.radius:
		return {"hit": false}

	var dist = sqrt(dist_sq)
	var normal = Vector3.UP

	if dist > 1e-05:
		normal = delta / dist

	var penetration = sphere.radius - dist

	return {
		"hit": true, 
		"normal": normal, 
		"overlap": penetration
	}

func collision_box_box(obj_a, obj_b):
	var smallest_overlap = INF
	var collision_normal = Vector3.ZERO

	var axes = get_sat_axes(obj_a, obj_b, )
	for axis in axes:
		var overlap = test_axis_overlap(obj_a, obj_b, axis)
		if overlap < 0:
			return {
				"hit": false, 
				"normal": collision_normal, 
				"overlap": smallest_overlap
			}
		if overlap < smallest_overlap:
			smallest_overlap = overlap
			collision_normal = axis
	collision_normal = collision_normal.normalized()


	var center_dir = obj_b.global_position - obj_a.global_position
	if center_dir.dot(collision_normal) < 0:
		collision_normal = - collision_normal

	return {
		"hit": true, 
		"normal": collision_normal, 
		"overlap": smallest_overlap
	}



func closest_point_on_obb(box, point):
	var center = box.global_position
	var dir = point - center

	var result = center

	var axes = get_obj_axes(box)
	var extents = box.half_size

	for i in range(3):
		var distance = dir.dot(axes[i])

		distance = clamp(
			distance, 
			- extents[i], 
			extents[i]
		)

		result += axes[i] * distance

	return result

func get_inverse_inertia_world(obj):
	if obj.inverse_mass == 0.0:
		var zero = Basis()
		zero.x = Vector3.ZERO
		zero.y = Vector3.ZERO
		zero.z = Vector3.ZERO
		return zero

	var rot = obj.global_transform.basis
	return rot * obj.inverse_inertia_tensor * rot.transposed()

func estimated_contact_point(obj_a, obj_b, normal: Vector3) -> Vector3:
	var point_a = get_support_point(obj_a, - normal)
	var point_b = get_support_point(obj_b, normal)


	return (point_a + point_b) * 0.5

func get_support_point(obj, direction: Vector3) -> Vector3:
	var basis = obj.global_transform.basis
	var center = obj.global_position
	var result = center
	if obj is MySphere:
		result += direction * obj.radius
	elif obj is MyBox:
		var e = obj.half_size
		result += basis.x * sign(direction.dot(basis.x)) * e.x
		result += basis.y * sign(direction.dot(basis.y)) * e.y
		result += basis.z * sign(direction.dot(basis.z)) * e.z

	return result

func test_axis_overlap(obj_a, obj_b, axis: Vector3):

	var a_proj = obj_a.global_position.dot(axis)
	var b_proj = obj_b.global_position.dot(axis)

	var radius_a = compute_obb_radius(obj_a, axis)
	var radius_b = compute_obb_radius(obj_b, axis)


	var overlap = (radius_a + radius_b) - abs(a_proj - b_proj)

	return overlap

func compute_obb_radius(box: Node3D, axis: Vector3) -> float:
	var b = box.global_transform.basis

	var ux = b.x
	var uy = b.y
	var uz = b.z

	var e = box.half_size
	var r = e.x * abs(axis.dot(ux)) + e.y * abs(axis.dot(uy)) + e.z * abs(axis.dot(uz))
	return r

func get_obj_axes(box: Node3D) -> Array[Vector3]:
	var b = box.global_transform.basis
	return [
		b.x.normalized(), 
		b.y.normalized(), 
		b.z.normalized()
		]

func get_sat_axes(obj_a, obj_b) -> Array:
	var axes = []

	var a_axes = get_obj_axes(obj_a)
	var b_axes = get_obj_axes(obj_b)


	axes.append_array(a_axes)
	axes.append_array(b_axes)


	for a in a_axes:
		for b in b_axes:
			var cross = a.cross(b)

			if cross.length() > 0.0001:
				axes.append(cross.normalized())
	return axes

func get_resting_face_normal(obj) -> Vector3:
	var basis = obj.global_transform.basis

	match obj.resting_face:
		0: return basis.x.normalized()
		1: return - basis.x.normalized()
		2: return basis.y.normalized()
		3: return - basis.y.normalized()
		4: return basis.z.normalized()
		5: return - basis.z.normalized()

	return Vector3.UP

func debug_object(obj, tag: String):
	print("---- DEBUG [", tag, "] ----")
	print("obj:", obj)
	print("pos:", obj.global_position)
	print("vel:", obj.velocity)
	print("ang:", obj.angular_velocity)
	print("mass:", obj.mass)
	print("resting:", obj.is_resting)
	print("vel_len:", obj.velocity.length())
	print("ang_len:", obj.angular_velocity.length())
