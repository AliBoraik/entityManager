package ru.itis.controllers;

import org.springframework.web.bind.annotation.*;
import ru.itis.Models.Car;
import ru.itis.orm.EntityManager;
import ru.itis.orm.EntityManagerFactory;

@RestController
@RequestMapping("car")
public class CarController {


    private final EntityManagerFactory entityManagerFactory;

    public CarController(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @GetMapping("/")
    public void all() {
        System.out.println("from res = / ");
    }

    @RequestMapping(value = "/persist", method = RequestMethod.PUT)
    public void persist(@RequestBody Car car) throws Exception {

        System.out.println(car);

        EntityManager em = entityManagerFactory.getEntityManager();

        em.persist(car);

        entityManagerFactory.closeEntityManager();

    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public Car find(@RequestParam Long id) throws Exception {

        EntityManager em = entityManagerFactory.getEntityManager();
        return em.find(Car.class, id);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public void remove(@RequestBody Car car) throws Exception {
        EntityManager em = entityManagerFactory.getEntityManager();
        em.remove(car);
    }

    @RequestMapping(value = "/merge", method = RequestMethod.POST)
    public Car merge(@RequestBody Car car) throws Exception {
        EntityManager em = entityManagerFactory.getEntityManager();
        return em.merge(car);
    }

}
