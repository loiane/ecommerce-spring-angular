#!/bin/bash

# JaCoCo Code Coverage Report Script
# This script provides an easy way to generate and view code coverage reports

echo "🔍 JaCoCo Code Coverage Report"
echo "=============================="

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

cd "$(dirname "$0")"

echo "📊 Running tests with JaCoCo coverage..."

# Run tests excluding the integration test that requires Docker
mvn test -Dtest=CategorySpecificationTest,ProductSpecificationTest,ProductServiceTest

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Tests completed successfully!${NC}"

    # Check if coverage report exists
    if [ -f "target/site/jacoco/index.html" ]; then
        echo ""
        echo "📈 Coverage Report Generated:"
        echo "   HTML Report: target/site/jacoco/index.html"
        echo "   CSV Report:  target/site/jacoco/jacoco.csv"
        echo "   XML Report:  target/site/jacoco/jacoco.xml"

        # Parse coverage from CSV file
        if [ -f "target/site/jacoco/jacoco.csv" ]; then
            echo ""
            echo "📋 Coverage Summary:"

            # Calculate overall coverage
            tail -n +2 target/site/jacoco/jacoco.csv | awk -F',' '
            BEGIN {
                total_instructions = 0
                covered_instructions = 0
                total_branches = 0
                covered_branches = 0
                total_lines = 0
                covered_lines = 0
            }
            {
                total_instructions += $4 + $5
                covered_instructions += $5
                total_branches += $6 + $7
                covered_branches += $7
                total_lines += $8 + $9
                covered_lines += $9
            }
            END {
                if (total_instructions > 0) {
                    instruction_coverage = (covered_instructions / total_instructions) * 100
                    printf "   Instructions: %.1f%% (%d/%d)\n", instruction_coverage, covered_instructions, total_instructions
                }
                if (total_branches > 0) {
                    branch_coverage = (covered_branches / total_branches) * 100
                    printf "   Branches:     %.1f%% (%d/%d)\n", branch_coverage, covered_branches, total_branches
                }
                if (total_lines > 0) {
                    line_coverage = (covered_lines / total_lines) * 100
                    printf "   Lines:        %.1f%% (%d/%d)\n", line_coverage, covered_lines, total_lines
                }
            }'
        fi

        echo ""
        echo -e "${YELLOW}🌐 To view detailed report, open:${NC}"
        echo "   file://$(pwd)/target/site/jacoco/index.html"

    else
        echo -e "${RED}❌ Coverage report not found!${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ Tests failed!${NC}"
    exit 1
fi

echo ""
echo "🎯 Coverage Goals:"
echo "   - Instruction Coverage: ≥80%"
echo "   - Branch Coverage:      ≥70%"
echo ""
echo "💡 To improve coverage, add tests for:"
echo "   - CategoryService methods"
echo "   - CategoryController endpoints"
echo "   - ProductController endpoints"
echo "   - GlobalExceptionHandler error scenarios"
