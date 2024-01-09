package se.sundsvall.byggrarchiver.testutils;

import org.mockito.ArgumentMatcher;

import generated.se.sundsvall.arendeexport.BatchFilter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BatchFilterMatcher implements ArgumentMatcher<BatchFilter> {

    private BatchFilter left;

    @Override
    public boolean matches(final BatchFilter right) {
        if (right == null) {
            return false;
        }

        return left.getLowerExclusiveBound().isEqual(right.getLowerExclusiveBound()) &&
                left.getUpperInclusiveBound().isEqual(right.getUpperInclusiveBound());
    }
}
