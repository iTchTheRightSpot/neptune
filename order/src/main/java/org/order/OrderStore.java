package org.order;

import org.springframework.data.repository.CrudRepository;

interface OrderStore extends CrudRepository<Order, Integer> {}