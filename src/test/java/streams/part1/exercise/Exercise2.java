package streams.part1.exercise;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

@SuppressWarnings({"ConstantConditions", "unused"})
public class Exercise2 {

    @Test
    public void calcAverageAgeOfEmployees() {
        List<Employee> employees = getEmployees();

        Double expected = employees.stream()
                .map(Employee::getPerson)
                .map(Person::getAge)
                .mapToDouble(Double::valueOf)
                .average()
                .orElseThrow(NoSuchElementException::new);

        assertEquals(33.66, expected, 0.1);
    }

    private static int compareByNameLength(Person leftPerson, Person rightPerson) {
        return Integer.compare(
                leftPerson.getFullName().length(),
                rightPerson.getFullName().length());
    }

    @Test
    public void findPersonWithLongestFullName() {
        List<Employee> employees = getEmployees();

        Person expected = employees.stream()
                .map(Employee::getPerson)
                .max(Exercise2::compareByNameLength)
                .orElseThrow(NoSuchElementException::new);

        assertEquals(expected, employees.get(1).getPerson());
    }

    @Test
    public void findEmployeeWithMaximumDurationAtOnePosition() {
        List<Employee> employees = getEmployees();

        ToIntFunction<Employee> getMaxDuration = employee -> employee.getJobHistory().stream()
                .mapToInt(JobHistoryEntry::getDuration)
                .max()
                .orElseThrow(NoSuchElementException::new);

        Employee expected = employees.stream()
                .max(Comparator.comparingInt(getMaxDuration))
                .orElseThrow(NoSuchElementException::new);

        assertEquals(expected, employees.get(4));
    }

    /**
     * Вычислить общую сумму заработной платы для сотрудников. Базовая ставка каждого сотрудника
     * составляет 75_000. Если на текущей позиции (последняя в списке) он работает больше трех лет -
     * ставка увеличивается на 20%
     */
    @Test
    public void calcTotalSalaryWithCoefficientWorkExperience() {
        List<Employee> employees = getEmployees();

        ToDoubleFunction<Stream<JobHistoryEntry>> calculateEmployeeSalary = durationStream -> {
            int durationOfCurrentPosition = durationStream
                    .mapToInt(JobHistoryEntry::getDuration)
                    .reduce(0, (left, right) -> right);
            return 75_000 * (durationOfCurrentPosition > 3 ? 1.2 : 1);
        };

        Double expected = employees.stream()
                .map(Employee::getJobHistory)
                .map(Collection::stream)
                .mapToDouble(calculateEmployeeSalary)
                .reduce(Double::sum)
                .orElseThrow(NoSuchElementException::new);

        assertEquals(465000.0, expected, 0.001);
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }
}