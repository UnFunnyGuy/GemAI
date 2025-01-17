package com.sarath.gem.core.base

/**
 * An interface defining the contract for mapping between an Entity and a Domain object.
 *
 * This interface provides two methods, `mapToDomain` and `mapToEntity`, for bidirectional mapping between data transfer
 * objects (Entities) and domain models (Domain objects). Implementations of this interface should handle the specific
 * transformations required for each mapping direction.
 *
 * @param Entity The type representing the data transfer object (e.g., database entity).
 * @param Domain The type representing the domain model object.
 */
interface BaseMapper<Entity, Domain> {
    /**
     * Maps an [Entity] object to a [Domain] object.
     *
     * This function takes an entity object as input and transforms its data into a corresponding domain object. The
     * specific mapping logic depends on the structure of the [Entity] and [Domain] classes and should be implemented
     * within the function's body.
     *
     * @param entity The [Entity] object to be mapped.
     * @return A new [Domain] object representing the mapped data.
     */
    fun mapToDomain(entity: Entity): Domain

    /**
     * Maps a [Domain] object to an [Entity] object.
     *
     * This function takes a domain object containing data and transforms it into an entity object, which is typically
     * used for data persistence or storage. The specific mapping logic will depend on the structure of the [Domain] and
     * [Entity] classes.
     *
     * @param domain The [Domain] object to be mapped.
     * @return The resulting [Entity] object.
     */
    fun mapToEntity(domain: Domain): Entity
}
